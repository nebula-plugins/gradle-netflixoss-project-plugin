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
package nebula.plugin.netflixossproject

import nebula.test.IntegrationSpec
import org.ajoberstar.grgit.Grgit
import org.gradle.api.plugins.JavaPlugin

class NetflixOssProjectIntegrationSpec extends IntegrationSpec {
    Grgit grgit

    def setup() {
        grgit = Grgit.init(dir: projectDir)

        buildFile << """
            ext.dryRun = true
            ${applyPlugin(NetflixOssProjectPlugin)}
            ${applyPlugin(JavaPlugin)}
        """.stripIndent()

        grgit.add(patterns: ['build.gradle'])
        grgit.commit(message: 'Setup')
    }

    def 'run build'() {
        when:
        runTasksSuccessfully('build')

        then:
        noExceptionThrown()
    }
}
