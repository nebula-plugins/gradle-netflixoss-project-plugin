gradle-netflixoss-project-plugin
================================
[![Build Status](https://travis-ci.org/nebula-plugins/gradle-netflixoss-project-plugin.svg?branch=master)](https://travis-ci.org/nebula-plugins/projects/gradle-netflixoss-project-plugin)
[![Coverage Status](https://coveralls.io/repos/nebula-plugins/gradle-netflixoss-project-plugin/badge.svg?branch=masterservice=github)](https://coveralls.io/github/nebula-plugins/projects/gradle-netflixoss-project-plugin?branch=master)
[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/nebula-plugins/gradle-netflixoss-project-plugin?utm_source=badgeutm_medium=badgeutm_campaign=pr-badge)
[![Apache 2.0](https://img.shields.io/github/license/nebula-plugins/gradle-netflixoss-project-plugin.svg)](http://www.apache.org/licenses/LICENSE-2.0)


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
  * com.netflix.nebula:gradle-contacts-plugin - to add developers to publications
  * com.netflix.nebula:nebula-project-plugin
  * com.netflix.nebula:nebula-bintray-plugin - wraps gradle-bintray-plugin with different defaults and adding OJO support and multi-module support.
  * com.netflix.nebula:nebula-publishing-plugin - for producing a jar, source jar, javadoc jar with metadata about how it was produced.
  * com.netflix.nebula:nebula-release-plugin - for providing release tasks, versioning, and tagging
  * com.netflix.nebula:nebula-test - for Gradle integration tests.
  * nl.javadude.gradle.plugins:license-gradle-plugin - for license recommendations

# Applying the Plugin

This plugin was tested with Gradle 2.2.1

To include, add the following to your build.gradle

If using gradle 2.1 or newer:

    plugins {
      id 'nebula.netflixoss' version '2.2.10'
    }

-or-

    buildscript {
      repositories { jcenter() }
      dependencies { classpath 'com.netflix.nebula:gradle-netflixoss-project-plugin:2.2.10' }
    }

    allprojects {
        apply plugin: 'nebula.netflixoss'
    }

# Defaults

These are some of the defaults that are set.

* sourceCompatibility 1.7: Please change if you want to publish to older or newer java versions
* release.scope set to minor: By default always bump the minor version
* a placeholder developer is added to the contacts section: to add more see [gradle-contacts-plugin](https://github.com/nebula-plugins/gradle-contacts-plugin)


    contacts {
      'myemail@sample.org' {
        github '<githubid>'
        moniker 'First Last'
      }
    }

# Variants

## License Check

By default the license check is on. To turn it off:

    license {
        ignoreFailures = true
    }

# Release Task Information and Configuration

see [nebula-release-plugin](https://github.com/nebula-plugins/nebula-release-plugin)

We disable the devSnapshot task since we release to oss.jfrog.org with maven style -SNAPSHOT versions.

### Release Tasks

* snapshot - release a snapshot version, does not create a tag, the version will be `major.minor.patch-SNAPSHOT`
* candidate - release a candidate version, creates a tag, the version will be `major.minor.patch-rc.#`
* final - release a final version, creates a tag, the version will be `major.minor.patch`

### Properties to change the version

-Prelease.scope - can be used to change which part of the version string is changed

* major - If last tag was v1.2.3 the release would go to v2.0.0
* minor - If last tag was v1.2.3 the release would go to v1.3.0, this is the default
* patch - If last tag was v1.2.3 the release would go to v1.2.4

### Properties to disable branch checking for travisci releases off existing tags

-Prelease.travisci - takes a boolean, true disables the prepare checks and release tagging, false(the default) leaves the normal checks in place.
