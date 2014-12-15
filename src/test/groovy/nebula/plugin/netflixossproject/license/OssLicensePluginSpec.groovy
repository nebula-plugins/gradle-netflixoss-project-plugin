/*
 * Copyright 2014 Netflix, Inc.
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

import nebula.test.ProjectSpec
import org.gradle.api.plugins.JavaPlugin

class OssLicensePluginSpec extends ProjectSpec {
    def 'lazily save file'() {
        when:
        project.plugins.apply(JavaPlugin)
        project.plugins.apply(OssLicensePlugin)

        then:
        def HEADER = new File(projectDir, 'build/license/HEADER')
        !HEADER.exists()

        when:
        def headerTask = project.tasks.getByName('writeLicenseHeader')
        headerTask.getActions().each { it.execute(headerTask) }

        then:
        HEADER.exists()
    }
}
