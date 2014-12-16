gradle-netflixoss-project-plugin
================================

Gradle plugin to setup common needs for Netflix OSS projects

This plugin is to support projects in the NetflixOSS org (and it isn't meant to be used elsewhere). It is at its essence
just a combination of other plugins that are common to all NetflixOSS projects, with some additional configuration. The
primary responsibilities is to:

  * Provide release process
  * Configure publishing
  * Recommend license headers
  * Add some error handling for javadoc in jdk8

This project could be used as an example of how a "project plugin" could work. A "project plugin" is a Gradle plugin that
provides consistency across many projects, e.g. in a Github org or an enterprise.

# Plugins Used

For reference, these are Gradle-related modules used:

  * com.netflix.nebula:gradle-dependency-lock-plugin - allow project to declare their ideal state of dependencies and lock them to specific versions for releases.
  * com.netflix.nebula:nebula-project-plugin
  * com.netflix.nebula:nebula-bintray-plugin - wraps gradle-bintray-plugin with different defaults and adding OJO support and multi-module support.
  * com.netflix.nebula:nebula-publishing-plugin - for producing a jar, source jar, javadoc jar with metadata about how it was produced.
  * nl.javadude.gradle.plugins:license-gradle-plugin - for license recommendations
  * com.netflix.nebula:nebula-release-plugin - for providing release tasks, versioning, and tagging
  * com.netflix.nebula:nebula-test - for Gradle integration tests.

# Applying the Plugin

To include, add the following to your build.gradle

If using gradle 2.1 or newer:

    plugins {
      id 'nebula.netflixoss' version '2.2.0'
    }

-or-

    buildscript {
      repositories { jcenter() }
      dependencies { classpath 'com.netflix.nebula:gradle-netflixoss-project-plugin:2.2.0' }
    }

    allprojects {
        apply plugin: 'nebula.netflixoss'
    }

# Variants

## License Check

By default the license check is on. To turn it off:

    license {
        ignoreFailures = true
    }

# Release Task Information and Configuration

see [nebula-release-plugin](https://github.com/nebula-plugins/nebula-release-plugin)
