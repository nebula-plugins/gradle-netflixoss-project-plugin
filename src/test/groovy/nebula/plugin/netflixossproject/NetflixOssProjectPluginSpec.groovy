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

import nebula.plugin.contacts.BaseContactsPlugin
import nebula.plugin.contacts.ContactsPlugin
import nebula.plugin.dependencylock.DependencyLockPlugin
import nebula.plugin.info.InfoPlugin
import nebula.plugin.netflixossproject.license.OssLicensePlugin
import nebula.plugin.publishing.maven.MavenPublishPlugin
import nebula.plugin.publishing.publications.JavadocJarPlugin
import nebula.plugin.publishing.publications.SourceJarPlugin
import nebula.test.PluginProjectSpec
import org.ajoberstar.grgit.Grgit
import org.gradle.api.JavaVersion
import org.gradle.api.plugins.JavaPlugin
import org.gradle.plugins.ide.eclipse.EclipsePlugin
import org.gradle.plugins.ide.idea.IdeaPlugin
import spock.lang.Unroll

class NetflixOssProjectPluginSpec extends PluginProjectSpec {
    String pluginName = 'nebula.netflixoss'

    def setup() {
        def git = Grgit.init(dir: projectDir)
        git.commit(message: 'initial')
    }

    @Unroll
    def 'plugin adds #plugin successfully to a simple project'() {
        project.plugins.apply(NetflixOssProjectPlugin)

        expect:
        project.plugins.findPlugin(plugin) != null

        where:
        plugin | _
        MavenPublishPlugin | _
        JavadocJarPlugin | _
        SourceJarPlugin | _
        InfoPlugin | _
        ContactsPlugin | _
        DependencyLockPlugin | _
        OssLicensePlugin | _
        EclipsePlugin | _
        IdeaPlugin | _
    }

    def 'plugin sets default java version'() {
        project.plugins.apply(NetflixOssProjectPlugin)

        when:
        project.plugins.apply(JavaPlugin)

        then:
        project.sourceCompatibility == JavaVersion.VERSION_1_8
        project.targetCompatibility == JavaVersion.VERSION_1_8
    }

    def 'plugin allows changing java version'() {
        project.plugins.apply(NetflixOssProjectPlugin)
        project.plugins.apply(JavaPlugin)

        when:
        project.sourceCompatibility = '1.9'
        project.targetCompatibility = '1.9'

        then:
        project.sourceCompatibility == JavaVersion.VERSION_1_9
        project.targetCompatibility == JavaVersion.VERSION_1_9
    }

    def 'plugin allows changing java version via sourceCompatibility only'() {
        project.plugins.apply(NetflixOssProjectPlugin)
        project.plugins.apply(JavaPlugin)

        when:
        project.sourceCompatibility = '1.9'

        then:
        project.sourceCompatibility == JavaVersion.VERSION_1_9
        project.targetCompatibility == JavaVersion.VERSION_1_9
    }

    def 'plugin sets default group'() {
        when:
        project.plugins.apply NetflixOssProjectPlugin

        then:
        project.group == 'com.netflix'
    }

    def 'plugin allows change of default group before applying plugin'() {
        project.group = 'test.netflix'

        when:
        project.plugins.apply NetflixOssProjectPlugin

        then:
        project.group == 'test.netflix'
    }

    def 'plugin allows change of default group after applying plugin'() {
        project.plugins.apply NetflixOssProjectPlugin

        when:
        project.group = 'after.netflix'

        then:
        project.group == 'after.netflix'
    }

    def 'stock developer in place'() {
        when:
        project.plugins.apply NetflixOssProjectPlugin

        then:
        def people = project.plugins.getPlugin(BaseContactsPlugin).extension.people
        people.size() == 1
        people.values().any { it.email == 'netflixoss@netflix.com' }
    }
}
