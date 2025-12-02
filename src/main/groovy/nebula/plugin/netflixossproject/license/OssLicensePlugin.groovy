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
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider

/**
 * Leverage license plugin to show missing headers, and inject license into the POM
 */
class OssLicensePlugin  implements Plugin<Project> {
    Project project

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

        def headerFile = defineHeaderFile()
        licenseExtension.header = headerFile.get().asFile

        def writeTask = project.tasks.register('writeLicenseHeader') {
            description = 'Write license header for License tasks'
            def outputFile = headerFile
            outputs.file(outputFile)

            doFirst {
                outputFile.get().asFile.parentFile.mkdirs()
                copyHeaderFile(outputFile.get().asFile)
            }
        }
        project.tasks.withType(License).configureEach {
            it.dependsOn(writeTask)
        }

    }

    Provider<RegularFile> defineHeaderFile() {
        project.layout.buildDirectory.file('license/HEADER')
    }

    def copyHeaderFile(File headerFile) {
        this.class.classLoader.getResourceAsStream('netflixoss/HEADER').withStream { input ->
            headerFile.withOutputStream { out ->
                out << input
            }
        }
    }
}
