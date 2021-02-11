package nebula.plugin.netflixossproject

import nebula.test.IntegrationSpec
import org.ajoberstar.grgit.Grgit

import java.nio.file.Files

abstract class GitVersioningIntegrationSpec extends IntegrationSpec {
    protected Grgit git
    protected Grgit originGit

    def setup() {
        def origin = new File(projectDir.parent, "${projectDir.name}.git")
        if (origin.exists()) {
            origin.deleteDir()
        }
        origin.mkdirs()

        ['build.gradle', 'settings.gradle'].each {
            Files.move(new File(projectDir, it).toPath(), new File(origin, it).toPath())
        }

        originGit = Grgit.init(dir: origin)

        originGit.add(patterns: ['build.gradle', 'settings.gradle', '.gitignore', 'gradle.properties'] as Set)
        originGit.commit(message: 'Initial checkout')

        git = Grgit.clone(dir: projectDir, uri: origin.absolutePath) as Grgit

        new File(projectDir, '.gitignore') << '''
            .gradle-test-kit
            .gradle
            build/
            gradle.properties
        '''.stripIndent()

        setupBuild()

        git.commit(message: 'Setup')
        git.push()
    }

    abstract def setupBuild()

    def cleanup() {
        if (git) git.close()
        if (originGit) originGit.close()
    }
}
