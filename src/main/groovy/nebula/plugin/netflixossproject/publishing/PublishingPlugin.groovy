/*
 * Copyright 2014-2019 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nebula.plugin.netflixossproject.publishing


import nebula.plugin.bintray.BintrayExtension
import nebula.plugin.bintray.BintrayPlugin
import nebula.plugin.bintray.NebulaBintrayPackageTask
import nebula.plugin.bintray.NebulaBintrayVersionTask
import nebula.plugin.bintray.NebulaGpgSignVersionTask
import nebula.plugin.bintray.NebulaMavenCentralVersionSyncTask
import nebula.plugin.info.scm.ScmInfoExtension
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionGraph
import org.gradle.api.publish.tasks.GenerateModuleMetadata
import org.gradle.api.tasks.Upload

class PublishingPlugin implements Plugin<Project> {

    public static final String NETFLIXOSS_ALT_CANDIDATE_REPO = 'netflixossAltCandidateRepo'

    @Override
    void apply(Project project) {
        boolean dryRun = project.hasProperty('dryRun') && project.property('dryRun') as Boolean
        def disable = {
            it.enabled = !dryRun
        }

        def runOnlyForCandidateAndFinal = { Task task ->
            project.gradle.taskGraph.whenReady { TaskExecutionGraph graph ->
                task.onlyIf {
                    graph.hasTask(':final') || graph.hasTask(':candidate')
                }
            }
        }
        project.plugins.apply org.gradle.api.publish.plugins.PublishingPlugin
        project.plugins.apply BintrayPlugin
        project.tasks.withType(NebulaBintrayPackageTask, disable)
        project.tasks.withType(NebulaBintrayPackageTask, runOnlyForCandidateAndFinal)
        project.tasks.withType(NebulaMavenCentralVersionSyncTask, disable)
        project.tasks.withType(NebulaMavenCentralVersionSyncTask, runOnlyForCandidateAndFinal)
        project.tasks.withType(NebulaGpgSignVersionTask, disable)
        project.tasks.withType(NebulaGpgSignVersionTask, runOnlyForCandidateAndFinal)
        project.tasks.withType(NebulaBintrayVersionTask, disable)
        project.tasks.withType(NebulaBintrayVersionTask, runOnlyForCandidateAndFinal)
        project.tasks.withType(Upload, disable)

        BintrayExtension bintray = project.rootProject == project ? project.extensions.getByType(BintrayExtension) : project.rootProject.extensions.getByType(BintrayExtension)
        bintray.with {
            if(shouldUseSnapshotRepo(project)) {
                repo.set('oss-snapshot-local')
                apiUrl.set('https://oss.jfrog.org/artifactory')
                syncToMavenCentral.set(false)
            } else if (shouldUseCandidateRepo(project)) {
                repo.set('oss-candidate')
                syncToMavenCentral.set(false)
            } else {
                repo.set('maven')
            }
            userOrg.set('netflixoss')
            licenses.set(['Apache-2.0'])
            labels.set(['netflixoss'])
        }

        ScmInfoExtension scmInfo = project.extensions.findByType(ScmInfoExtension)
        // We have to change the task directly, since they already copied from the extension in an afterEvaluate

        if (scmInfo) {
            // Assuming scmInfo.origin is something like git@github.com:netflix/project.git
            bintray.pkgName.set(calculateRepoFromOrigin(scmInfo.origin) ?: project.rootProject.name)
            def url = calculateUrlFromOrigin(scmInfo.origin)
            bintray.websiteUrl.set(url)
            bintray.issueTrackerUrl.set( "${url}/issues")
            bintray.vcsUrl.set("${url}.git")
        }

        project.plugins.withId('com.github.johnrengelman.shadow') {
            disableGradleModuleMetadataTask(project)
        }
    }

    static GIT_PATTERN = /((git|ssh|https?):(\/\/))?(\w+@)?([\w\.]+)([\:\\/])([\w\.@\:\/\-~]+)(\/)?/

    /**
     * Convert git syntax of git@github.com:reactivex/rxjava-core.git to https://github.com/reactivex/rxjava-core
     * @param origin
     */
    static String calculateUrlFromOrigin(String origin) {
        def m = origin =~ GIT_PATTERN
        return "https://${m[0][5]}/" + (m[0][7] - '.git')
    }

    static String calculateRepoFromOrigin(String origin) {
        def m = origin =~ GIT_PATTERN
        String path = m[0][7] - '.git'
        path.tokenize('/').last()
    }

    static Boolean shouldUseSnapshotRepo(Project project) {
        return project.gradle.startParameter.taskNames.contains('snapshot') || project.gradle.startParameter.taskNames.contains(':snapshot')
    }

    static Boolean shouldUseCandidateRepo(Project project) {
        if (!(project.gradle.startParameter.taskNames.contains('candidate') || project.gradle.startParameter.taskNames.contains(':candidate'))) {
            return false
        }

        if (project.hasProperty(NETFLIXOSS_ALT_CANDIDATE_REPO)) {
            def myproperty = project.property(NETFLIXOSS_ALT_CANDIDATE_REPO)
            return (myproperty instanceof String) ? myproperty.toBoolean() : myproperty
        }

        return true
    }

    private void disableGradleModuleMetadataTask(Project project) {
        project.tasks.withType(GenerateModuleMetadata).configureEach(new Action<GenerateModuleMetadata>() {
            @Override
            void execute(GenerateModuleMetadata generateModuleMetadataTask) {
                generateModuleMetadataTask.enabled = false
            }
        })
    }
}
