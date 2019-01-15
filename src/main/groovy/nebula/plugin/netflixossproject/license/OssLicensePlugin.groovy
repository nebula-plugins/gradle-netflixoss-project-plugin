/*
 * Copyright 2015-2019 Netflix, Inc.
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
package nebula.plugin.netflixossproject.license

import nebula.plugin.publishing.maven.license.MavenApacheLicensePlugin
import nl.javadude.gradle.plugins.license.License
import nl.javadude.gradle.plugins.license.LicenseExtension
import nl.javadude.gradle.plugins.license.LicensePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Leverage license plugin to show missing headers, and inject license into the POM
 */
class OssLicensePlugin  implements Plugin<Project> {
    Project project
    File header

    @Override
    void apply(Project project) {
        this.project = project

        project.plugins.apply(MavenApacheLicensePlugin)
        project.plugins.apply(LicensePlugin)
        def licenseExtension = project.extensions.getByType(LicenseExtension)
        licenseExtension.skipExistingHeaders = true
        licenseExtension.strictCheck = false
        licenseExtension.ignoreFailures = true
        licenseExtension.ext.year = Calendar.getInstance().get(Calendar.YEAR)
        licenseExtension.excludes(['**/*.txt', '**/*.conf', '**/*.json', '**/*.properties'])

        header = defineHeaderFile()
        licenseExtension.header = header

        def writeTask = project.task('writeLicenseHeader') {
            description 'Write license header for License tasks'
            onlyIf {
                def licenseTasks = project.gradle.taskGraph.getAllTasks().findAll { it instanceof License }
                return licenseTasks.any { ((License) it).getHeader() == header }
            }
            doFirst {
                header.parentFile.mkdirs()
                copyHeaderFile()
            }
        }
        project.tasks.withType(License) {
            it.dependsOn(writeTask)
        }

    }

    File defineHeaderFile() {
        File tmpDir = new File(project.getBuildDir(), 'license')
        tmpDir.mkdirs()
        new File(tmpDir, 'HEADER')
    }

    def copyHeaderFile() {
        this.class.classLoader.getResourceAsStream('netflixoss/HEADER').withStream { input ->
            header.withOutputStream { out ->
                out << input
            }
        }
    }
}
