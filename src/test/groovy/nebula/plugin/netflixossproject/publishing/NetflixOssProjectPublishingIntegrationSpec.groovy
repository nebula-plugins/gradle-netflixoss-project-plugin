/*
 * Copyright 2021 Netflix, Inc.
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

import nebula.plugin.netflixossproject.GitVersioningIntegrationSpec
import nebula.plugin.netflixossproject.NetflixOssProjectPlugin
import org.gradle.api.plugins.JavaPlugin

class NetflixOssProjectPublishingIntegrationSpec extends GitVersioningIntegrationSpec {

    @Override
    def setupBuild() {
        buildFile << """
            group = 'test.nebula'
            ${applyPlugin(NetflixOssProjectPlugin)}
            ${applyPlugin(JavaPlugin)}
            repositories {
                mavenCentral()
            }
            dependencies {
                testImplementation 'junit:junit:4.12'
            }
            """.stripIndent()
    }

    def 'snapshot task invokes publication to NetflixOSS repository'() {
        when:
        def result = runTasksSuccessfully('snapshot', '-PnetflixOss.username=user',  '-PnetflixOss.password=password', '--dry-run')

        then:
        result.standardOutput.contains(':publishNebulaPublicationToNetflixOSSRepository SKIPPED')
    }

    def 'candidate task invokes publication to NetflixOSS repository'() {
        when:
        git.add(patterns: ['.'] as Set)
        git.commit(message: 'Setup 2')
        git.tag.add(name: 'v0.0.1-rc.1')

        def result = runTasksSuccessfully('candidate', '-PnetflixOss.username=user',  '-PnetflixOss.password=password', '--dry-run')

        then:
        result.standardOutput.contains(':publishNebulaPublicationToNetflixOSSRepository SKIPPED')
    }

    def 'final task invokes publication to NetflixOSS repository and sonatype'() {
        when:
        git.add(patterns: ['.'] as Set)
        git.commit(message: 'Setup 3')
        git.tag.add(name: 'v0.0.1')

        def result = runTasksSuccessfully('final', '-PnetflixOss.username=user',  '-PnetflixOss.password=password', '-Psonatype.username=user',  '-Psonatype.password=password', '-Psonatype.signingKey=user',  '-Psonatype.signingPassword=password','--dry-run')

        then:
        result.standardOutput.contains(':signNebulaPublication SKIPPED')
        result.standardOutput.contains(':publishNebulaPublicationToNetflixOSSRepository SKIPPED')
        result.standardOutput.contains(':initializeSonatypeStagingRepository SKIPPED')
        result.standardOutput.contains(':publishNebulaPublicationToSonatypeRepository SKIPPED')
        result.standardOutput.contains(':closeSonatypeStagingRepository SKIPPED')
        result.standardOutput.contains(':releaseSonatypeStagingRepository SKIPPED')
        result.standardOutput.contains(':closeAndReleaseSonatypeStagingRepository SKIPPED')
    }
}
