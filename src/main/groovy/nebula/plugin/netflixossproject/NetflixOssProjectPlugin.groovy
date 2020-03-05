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
package nebula.plugin.netflixossproject

import nebula.core.ProjectType
import nebula.plugin.compile.JavaCrossCompilePlugin
import nebula.plugin.contacts.ContactsPlugin
import nebula.plugin.dependencylock.DependencyLockPlugin
import nebula.plugin.info.InfoPlugin
import nebula.plugin.netflixossproject.license.OssLicensePlugin
import nebula.plugin.netflixossproject.publishing.PublishingPlugin
import nebula.plugin.publishing.maven.MavenPublishPlugin
import nebula.plugin.publishing.publications.JavadocJarPlugin
import nebula.plugin.publishing.publications.SourceJarPlugin
import nebula.plugin.release.NetflixOssStrategies
import nebula.plugin.release.ReleasePlugin
import nebula.plugin.release.git.base.ReleasePluginExtension
import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.execution.TaskExecutionGraph
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository
import org.gradle.plugins.ide.eclipse.EclipsePlugin
import org.gradle.plugins.ide.idea.IdeaPlugin

class NetflixOssProjectPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        if (!project.group) {
            project.group = 'com.netflix'
        }
        project.description = project.name
        ProjectType type = new ProjectType(project)
        project.plugins.apply JavaCrossCompilePlugin

        if (type.isLeafProject || type.isRootProject) {
            project.plugins.apply ReleasePlugin
            if (type.isRootProject) {
                ReleasePluginExtension releaseExtension = project.extensions.findByType(ReleasePluginExtension)
                releaseExtension?.defaultVersionStrategy = NetflixOssStrategies.SNAPSHOT(project)
            }
            if (type.isLeafProject) {
                project.plugins.apply MavenPublishPlugin
                project.plugins.apply JavadocJarPlugin
                project.plugins.apply SourceJarPlugin
            }
            project.plugins.apply PublishingPlugin
            project.plugins.apply DependencyLockPlugin
        }

        if (type.isRootProject) {
            project.gradle.taskGraph.whenReady { TaskExecutionGraph graph ->
                if (graph.hasTask(':devSnapshot') || graph.hasTask(':immutableSnapshot')) {
                    throw new GradleException('You cannot use the devSnapshot or immutableSnapshot task from the release plugin. Please use the snapshot task.')
                }

            }

            def collectNetflixOSS = project.tasks.create('collectNetflixOSS')
            collectNetflixOSS.doLast {
                new File(project.buildDir, 'netflixoss').mkdirs()
                def netflixoss = new File(project.buildDir, 'netflixoss/netflixoss.txt')
                netflixoss.text = ''
                project.allprojects.each { Project proj ->
                    if (new File(proj.buildDir, 'libs').exists()) {
                        netflixoss.append "${proj.group}:${proj.name}:${proj.version}\n"
                    }
                }
            }
            project.plugins.withType(JavaBasePlugin) {
                collectNetflixOSS.mustRunAfter project.tasks.assemble
                project.tasks.build.dependsOn collectNetflixOSS
            }

        }

        if (type.isLeafProject) {

            project.plugins.apply OssLicensePlugin

            project.plugins.withType(JavaBasePlugin) {
                project.rootProject.tasks.collectNetflixOSS.mustRunAfter project.tasks.assemble
                project.tasks.build.dependsOn project.rootProject.tasks.collectNetflixOSS
            }

            project.plugins.withType(JavaPlugin) { JavaPlugin javaPlugin ->
                JavaPluginConvention convention = project.convention.getPlugin(JavaPluginConvention)
                convention.sourceCompatibility = JavaVersion.VERSION_1_8
            }
        }

        project.plugins.apply ContactsPlugin
        if (type.isRootProject) {
            project.contacts {
                'netflixoss@netflix.com' {
                    github 'netflixgithub'
                    moniker 'Netflix Open Source Development'
                }
            }
        }
        project.plugins.apply InfoPlugin
        project.plugins.apply IdeaPlugin
        project.plugins.apply EclipsePlugin


    }
}
