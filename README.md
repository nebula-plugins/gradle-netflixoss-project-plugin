gradle-netflixoss-project-plugin
================================
![Support Status](https://img.shields.io/badge/nebula-internal-lightgray.svg)
[![Build Status](https://travis-ci.org/nebula-plugins/gradle-netflixoss-project-plugin.svg?branch=master)](https://travis-ci.org/nebula-plugins/gradle-netflixoss-project-plugin)
[![Coverage Status](https://coveralls.io/repos/nebula-plugins/gradle-netflixoss-project-plugin/badge.svg?branch=master&service=github)](https://coveralls.io/github/nebula-plugins/gradle-netflixoss-project-plugin?branch=master)
[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/nebula-plugins/gradle-netflixoss-project-plugin?utm_source=badgeutm_medium=badgeutm_campaign=pr-badge)
[![Apache 2.0](https://img.shields.io/github/license/nebula-plugins/gradle-netflixoss-project-plugin.svg)](http://www.apache.org/licenses/LICENSE-2.0)


Gradle plugin to setup common needs for Netflix OSS projects

This plugin is to support projects in the NetflixOSS org (and it isn't meant to be used elsewhere). It is at its essence
just a combination of other plugins that are common to all NetflixOSS projects, with some additional configuration. The
primary responsibilities is to:

  * Provide release process
  * Configure publishing
  * Recommend license headers

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
  * nl.javadude.gradle.plugins:license-gradle-plugin - for license recommendations

# Applying the Plugin

This plugin was tested with Gradle 4.4

To include, add the following to your build.gradle

If using gradle 2.1 or newer:

    plugins {
      id 'nebula.netflixoss' version '5.1.1'
    }

-or-

    buildscript {
      repositories { jcenter() }
      dependencies { classpath 'com.netflix.nebula:gradle-netflixoss-project-plugin:5.1.1' }
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

NOTE: We disable the `devSnapshot` and  `immutableSnapshot` tasks since we release to oss.jfrog.org with maven style -SNAPSHOT versions and those tasks are using incompatible version patterns.

### Release Tasks

* `snapshot` - release a snapshot version, does not create a tag, the version will be `major.minor.patch-SNAPSHOT`
* `candidate` - release a candidate version, creates a tag, the version will be `major.minor.patch-rc.#`
* `final` - release a final version, creates a tag, the version will be `major.minor.patch`

### Properties to change the version

-Prelease.scope - can be used to change which part of the version string is changed

* major - If last tag was v1.2.3 the release would go to v2.0.0
* minor - If last tag was v1.2.3 the release would go to v1.3.0, this is the default
* patch - If last tag was v1.2.3 the release would go to v1.2.4

### Properties to disable branch checking for travisci releases off existing tags

-Prelease.travisci - takes a boolean, true disables the prepare checks and release tagging, false(the default) leaves the normal checks in place.
-Prelease.disableGitChecks - does the same as above

# Where We Publish by Default

`snapshot` builds are published to oss.jfrog.org
`candidate` we publish to a special candidate repo https://dl.bintray.com/netflixoss/oss-candidate
`final` we publish to jcenter and mavenCentral

### Build Property to Disable Alternative Publishing Repository

if the following property is in place and set to false we will publish to jcenter and mavenCentral instead of a staging candidate repo

    ./gradlew -PnetflixossAltCandidateRepo=false clean candidate

Gradle Compatibility Tested
---------------------------

Built with Oracle JDK7
Tested with Oracle JDK8

| Gradle Version | Works |
| :------------: | :---: |
| <= 4.3.1       | no    |
| 4.4            | yes   |

LICENSE
=======

Copyright 2014-2019 Netflix, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

<http://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
