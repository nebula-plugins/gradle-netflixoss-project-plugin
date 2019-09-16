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
package nebula.plugin.netflixossproject

import nebula.test.IntegrationSpec
import org.ajoberstar.grgit.Grgit
import org.gradle.api.plugins.JavaPlugin
import spock.lang.Ignore

class NetflixOssMultiProjectIntegrationSpec extends IntegrationSpec {
    Grgit grgit

    def setup() {
        grgit = Grgit.init(dir: projectDir)
        grgit.remote.add(name: 'origin', url: 'git@fake.com:project/project.git')

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

        grgit.add(patterns: ['build.gradle', 'settings.gradle', 'sub1/build.gradle', 'sub2/build.gradle'])
        grgit.commit(message: 'Setup')
    }

    def 'run build'() {
        when:
        runTasksSuccessfully('build')

        then:
        noExceptionThrown()
    }

    def 'verify contacts'() {
        when:
        runTasksSuccessfully('generatePomFileForNebulaPublication')

        then:
        def developers = new XmlSlurper().parse(new File(projectDir, 'sub1/build/publications/nebula/pom-default.xml')).developers
        def testNode = developers.developer.find { it.email == 'test@example.org'}
        testNode.id == 'test'
        testNode.name == 'Test Example'
    }

    def 'creates a manifest of published modules'() {
        def result = '''\
            test.nebula:sub1:0.1.0-SNAPSHOT
            test.nebula:sub2:0.1.0-SNAPSHOT
        '''.stripIndent()

        when:
        runTasksSuccessfully('build')

        then:
        new File(projectDir, 'build/netflixoss/netflixoss.txt').text.contains(result)
    }

    def 'release.travisci flag does not break builds when set to true'() {
        when:
        runTasksSuccessfully('build', '-Prelease.travisci=true')

        then:
        noExceptionThrown()
    }

    def 'make sure no pom is created in top level project'() {
        buildFile << '''\
            group = 'test.nebula'

            subprojects {
                publishing {
                    repositories {
                        maven {
                            name 'testRepo'
                            url '../build/testrepo'
                        }
                    }
                }
            }
        '''.stripIndent()
        when:
        runTasksSuccessfully('publishToMavenLocal', 'publishNebulaPublicationToTestRepoRepository')

        then:
        def files = new FileNameFinder().getFileNames("$projectDir/build/testrepo/test/nebula", '**/*.pom')
        files.size() == 2
    }

    def 'tasks runs without error'() {
        when:
        runTasksSuccessfully('tasks', '--all')

        then:
        noExceptionThrown()
    }
}
