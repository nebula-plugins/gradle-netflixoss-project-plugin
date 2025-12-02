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
import nebula.plugin.publishing.NebulaOssPublishingPlugin
import nebula.plugin.publishing.maven.MavenPublishPlugin
import nebula.plugin.publishing.publications.JavadocJarPlugin
import nebula.plugin.publishing.publications.SourceJarPlugin
import nebula.plugin.release.NetflixOssStrategies
import nebula.plugin.release.ReleasePlugin
import nebula.plugin.release.git.base.ReleasePluginExtension
import org.gradle.api.GradleException
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.execution.TaskExecutionGraph
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository
import org.gradle.api.tasks.TaskProvider
import org.gradle.plugins.ide.eclipse.EclipsePlugin

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
            project.tasks.configureEach { task ->
                if (task.name == 'devSnapshot' || task.name == 'immutableSnapshot') {
                    task.doFirst {
                        throw new GradleException('You cannot use the devSnapshot or immutableSnapshot task from the release plugin. Please use the snapshot task.')
                    }
                }
            }

            def collectNetflixOSS = project.tasks.register('collectNetflixOSS') {
                def outputDir = project.layout.buildDirectory.dir('netflixoss')
                def outputFile = outputDir.map { it.file('netflixoss.txt') }

                outputs.file(outputFile)

                doLast {
                    outputDir.get().asFile.mkdirs()
                    def netflixoss = outputFile.get().asFile
                    netflixoss.text = ''
                    project.allprojects.each { Project proj ->
                        if (proj.layout.buildDirectory.dir('libs').get().asFile.exists()) {
                            netflixoss.append "${proj.group}:${proj.name}:${proj.version}\n"
                        }
                    }
                }
            }
            project.plugins.withType(JavaBasePlugin) {
                collectNetflixOSS.configure {
                    mustRunAfter(project.tasks.named('assemble'))
                }
                project.tasks.named('build').configure {
                    dependsOn(collectNetflixOSS)
                }
            }

        }

        if (type.isLeafProject) {

            project.plugins.apply OssLicensePlugin

            project.plugins.withType(JavaBasePlugin) {
                project.rootProject.tasks.named('collectNetflixOSS').configure {
                    mustRunAfter(project.tasks.named('assemble'))
                }
                project.tasks.named('build').configure {
                    dependsOn(project.rootProject.tasks.named('collectNetflixOSS'))
                }
            }

            project.plugins.withType(JavaPlugin) { JavaPlugin javaPlugin ->
                project.afterEvaluate {
                    JavaPluginExtension javaPluginExtension = project.extensions.getByType(JavaPluginExtension)
                    //if users provided their own explicit toolchain we remove the older incompatible property
                    if (javaPluginExtension.toolchain.languageVersion.isPresent()) {
                        javaPluginExtension.sourceCompatibility = null
                    } else {
                        javaPluginExtension.sourceCompatibility = JavaVersion.VERSION_1_8
                    }
                }
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
        project.plugins.apply EclipsePlugin


        project.afterEvaluate {
            project.plugins.withId('com.gradle.plugin-publish') {
                //Disable marker tasks
                project.tasks.configureEach { task ->
                    if ((task.name.contains("Marker") && task.name.contains('Maven')) ||
                            task.name.contains("PluginMarkerMavenPublicationToNetflixOSSRepository") ||
                            task.name.contains("PluginMarkerMavenPublicationToSonatypeRepository") ||
                            task.name.contains("publishPluginMavenPublicationToNetflixOSSRepository") ||
                            task.name.contains("publishPluginMavenPublicationToSonatypeRepository")) {
                        task.enabled = false
                    }
                }

                TaskProvider validatePluginsTask = project.tasks.named('validatePlugins')
                TaskProvider publishPluginsTask = project.tasks.named('publishPlugins')
                project.plugins.withId('com.netflix.nebula.release') {
                    project.tasks.withType(PublishToMavenRepository).configureEach {
                        TaskProvider releaseTask = project.rootProject.tasks.named('release')
                        it.mustRunAfter(releaseTask)
                        it.dependsOn(validatePluginsTask)
                        it.dependsOn(publishPluginsTask)
                    }
                }
            }
        }

    }
}
