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

class NetflixOssMultiProjectPublishingIntegrationSpec extends GitVersioningIntegrationSpec {

    @Override
    def setupBuild() {

        buildFile << """
            ext.dryRun = true
            allprojects {
                ${applyPlugin(JavaPlugin)}
                ${applyPlugin(NetflixOssProjectPlugin)}
            }

            subprojects {
                group = 'test.nebula'
                ext.dryRun = true
                ${applyPlugin(JavaPlugin)}
            }

            contacts {
                'test@example.org' {
                    github 'test'
                    moniker 'Test Example'
                }
            }
        """.stripIndent()

        settingsFile << '''\
            rootProject.name = 'multiprojecttest'
        '''

        addSubproject('sub1', '// hello')
        addSubproject('sub2', '''\
            dependencies {
                implementation project(':sub1')
            }
        '''.stripIndent())
    }

    def 'snapshot task invokes publication to NetflixOSS repository'() {
        when:
        def result = runTasksSuccessfully('snapshot', '-PnetflixOss.username=user',  '-PnetflixOss.password=password', '--dry-run')

        then:
        result.standardOutput.contains(':sub1:publishNebulaPublicationToNetflixOSSRepository SKIPPED')
        result.standardOutput.contains(':sub2:publishNebulaPublicationToNetflixOSSRepository SKIPPED')
    }

    def 'candidate task invokes publication to NetflixOSS repository'() {
        when:
        git.add(patterns: ['.'] as Set)
        git.commit(message: 'Setup 2')
        git.tag.add(name: 'v0.0.1-rc.1')

        def result = runTasksSuccessfully('candidate', '-PnetflixOss.username=user',  '-PnetflixOss.password=password', '--dry-run')

        then:
        result.standardOutput.contains(':sub1:publishNebulaPublicationToNetflixOSSRepository SKIPPED')
        result.standardOutput.contains(':sub2:publishNebulaPublicationToNetflixOSSRepository SKIPPED')
    }

    def 'final task invokes publication to NetflixOSS repository and sonatype'() {
        when:
        git.add(patterns: ['.'] as Set)
        git.commit(message: 'Setup 3')
        git.tag.add(name: 'v0.0.1')

        def result = runTasksSuccessfully('final', '-PnetflixOss.username=user',  '-PnetflixOss.password=password', '-Psonatype.username=user',  '-Psonatype.password=password', '-Psonatype.signingKey=user',  '-Psonatype.signingPassword=password','--dry-run')

        then:
        result.standardOutput.contains(':sub1:signNebulaPublication SKIPPED')
        result.standardOutput.contains(':sub1:publishNebulaPublicationToNetflixOSSRepository SKIPPED')
        result.standardOutput.contains(':sub1:initializeSonatypeStagingRepository SKIPPED')
        result.standardOutput.contains(':sub1:publishNebulaPublicationToSonatypeRepository SKIPPED')
        result.standardOutput.contains(':sub2:signNebulaPublication SKIPPED')
        result.standardOutput.contains(':sub2:publishNebulaPublicationToNetflixOSSRepository SKIPPED')
        result.standardOutput.contains(':sub2:initializeSonatypeStagingRepository SKIPPED')
        result.standardOutput.contains(':sub2:publishNebulaPublicationToSonatypeRepository SKIPPED')
        !result.standardOutput.contains(':sub1:closeRepository SKIPPED')
        !result.standardOutput.contains(':sub1:releaseRepository SKIPPED')
        !result.standardOutput.contains(':sub1:closeAndReleaseRepository SKIPPED')
        !result.standardOutput.contains(':sub1:closeRepository SKIPPED')
        !result.standardOutput.contains(':sub1:releaseRepository SKIPPED')
        !result.standardOutput.contains(':sub1:closeAndReleaseRepository SKIPPED')
        result.standardOutput.contains(':closeRepository SKIPPED')
        result.standardOutput.contains(':releaseRepository SKIPPED')
        result.standardOutput.contains(':closeAndReleaseRepository SKIPPED')
    }
}
