/*
 * Copyright 2014-2015 Netflix, Inc.
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

import com.jfrog.bintray.gradle.BintrayExtension
import com.jfrog.bintray.gradle.BintrayUploadTask
import nebula.plugin.bintray.BintrayPlugin
import nebula.plugin.info.scm.ScmInfoExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionGraph
import org.gradle.api.tasks.Upload
import org.jfrog.gradle.plugin.artifactory.task.BuildInfoBaseTask

class PublishingPlugin implements Plugin<Project> {

    public static final String NETFLIXOSS_ALT_CANDIDATE_REPO = 'netflixossAltCandidateRepo'

    @Override
    void apply(Project project) {
        boolean dryRun = project.hasProperty('dryRun') && project.property('dryRun') as Boolean
        def disable = {
            it.enabled = !dryRun
        }

        project.plugins.apply org.gradle.api.publish.plugins.PublishingPlugin
        project.plugins.apply BintrayPlugin
        project.tasks.withType(BintrayUploadTask, disable)
        project.tasks.withType(BintrayUploadTask) { Task task ->
            project.gradle.taskGraph.whenReady { TaskExecutionGraph graph ->
                task.onlyIf {
                    graph.hasTask(':final') || graph.hasTask(':candidate')
                }
            }
        }
        project.tasks.withType(Upload, disable)
        project.tasks.withType(BuildInfoBaseTask, disable)
        project.tasks.withType(BuildInfoBaseTask) { Task task ->
            project.gradle.taskGraph.whenReady { TaskExecutionGraph graph ->
                task.onlyIf {
                    graph.hasTask(':snapshot') || graph.hasTask(':devSnapshot')
                }
            }
        }

        BintrayExtension bintray = project.extensions.getByType(BintrayExtension)
        bintray.pkg.with {
            repo = 'maven'
            if (shouldUseCandidateRepo(project)) {
                repo = 'oss-candidate'
                version.mavenCentralSync.sync = false
            }
            userOrg = 'netflixoss'
            licenses = ['Apache-2.0']
            labels = ['netflixoss']
        }

        BintrayUploadTask bintrayUpload = (BintrayUploadTask) project.tasks.find { it instanceof BintrayUploadTask }

        bintrayUpload.doFirst {
            ScmInfoExtension scmInfo = project.extensions.findByType(ScmInfoExtension)
            // We have to change the task directly, since they already copied from the extension in an afterEvaluate

            if (scmInfo) {
                // Assuming scmInfo.origin is something like git@github.com:netflix/project.git
                bintrayUpload.packageName = calculateRepoFromOrigin(scmInfo.origin) ?: project.rootProject.name

                def url = calculateUrlFromOrigin(scmInfo.origin)
                bintrayUpload.packageWebsiteUrl = url
                bintrayUpload.packageIssueTrackerUrl = "${url}/issues"
                bintrayUpload.packageVcsUrl = "${url}.git"
            }
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

    Boolean shouldUseCandidateRepo(Project project) {
        if (!(project.gradle.startParameter.taskNames.contains('candidate') || project.gradle.startParameter.taskNames.contains(':candidate'))) {
            return false
        }

        if (project.hasProperty(NETFLIXOSS_ALT_CANDIDATE_REPO)) {
            def myproperty = project.property(NETFLIXOSS_ALT_CANDIDATE_REPO)
            return (myproperty instanceof String) ? myproperty.toBoolean() : myproperty
        }

        return true
    }
}
