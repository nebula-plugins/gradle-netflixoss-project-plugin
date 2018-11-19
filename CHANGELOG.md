6.1.0 / 2018-11-19
==================

* Update to newer version of license plugin 0.14.0
* Now works with our facet plugin

4.0.1 / 2017-09-07
==================

* BUGFIX: Only set defaultVersionStrategy if nebula.release found
* Update gradle-java-cross-compile-plugin from 0.8.0 -> 0.9.0
* Update nebula-release-plugin from 6.0.0 -> 6.0.1
* Update gradle-git from 1.7.1 -> 1.7.2
* Update nebula-publishing-plugin from 5.1.1 -> 5.1.3

4.0.0 / 2017-08-09
==================

* BREAKING: Swap candidate to default to publish to candidate repo
* BREAKING: Requires jdk8

3.5.2 / 2016-11-11
==================

* BUGFIX: update gradle-info-plugin from 3.3.3 -> 3.3.4 to fix issue with empty configurations and manifest generation
* Update gradle-dependency-lock-plugin from 4.3.0 -> 4.3.1

3.5.1 / 2016-10-19
==================

* BUGFIX: Update where flag is read to swap to separate candidate repository

3.5.0 / 2016-10-13
==================

* Add ability to configure that candidate builds should go to a known non jcenter/mavenCentral repository
* Upgrade to gradle 3.1

3.4.0 / 2016-08-17
==================

* Add excludes on some file types so they'll be ignored by the license plugin
* Upgrade to gradle 3.0
* Update nebula-publishing-plugin to include `nebula.compile-api` to add `compileApi` configuration

3.3.0 / 2016-06-02
==================

* Update bintray/artifactory dependencies to fix tasks task

3.2.3 / 2016-02-09
==================

3.2.2 / 2016-02-05
==================

* Republish

3.2.1 / 2016-02-05
==================

* nebula.nebula-release 3.2.0->4.0.1
* nebula.nebula-bintray 3.3.1->3.3.3
* Fixes tasks task on multiprojects

3.2.0 / 2016-02-02
==================

* Update to latest bintray plugin
* Add tests to make sure `./gradlew tasks` succeeds

3.1.2 / 2015-09-21
==================

* BUGFIX: fix issue with publishing useless top level pom and breaking mavenCentral sync
* Upgrade nebula-release-plugin to 3.0.2 to fix rc release from travis

3.1.1 / 2015-09-12
==================

* Fix a plugin ordering bug that will cause projects to upload as unspecified

3.1.0 / 2015-09-11
==================

* Updated email address
* Remove block that was disabling javadoc linting

3.0.0 / 2015-09-10
==================

* move to gradle 2.6
* cleanup plugin due to changes in nebula-release-plugin and nebula-bintray-plugin
* nebula-release will no longer publish to bintray if tests are failing

2.2.10 / 2015-07-14
===================

* Update nebula-release to 2.2.7, calculate version once for multiproject

2.2.9 / 2015-03-12
==================

* Further modification to release.travisci to remove more checks.

2.2.8 / 2015-03-12
==================

* Update gradle-dependency-lock to 2.2.2
* Add release.travisci flag to disable some checks if building from a tag.

2.2.7 / 2015-02-09
==================

* Update nebula-release to 2.2.5 to pick up major.minor.x release branches
* Update all nebula dependencies to 2.2.x branch

2.2.6 / 2015-02-09
==================

* Didn't merge pull request

2.2.5 / 2015-01-20
==================

* Create a manifest of jars published by a project to ease internal importing

2.2.4 / 2015-01-13
==================

* Fix bintray snapshot publishing for multiprojects

2.2.3 / 2015-01-08
==================

* Update nebula-publishing-plugin to fix an issue with publishing war projects in a multiproject.

2.2.2 / 2015-01-02
==================

* Fix some issues with multiprojects
* Added a default developer to the contacts section
* Added some sanity integration tests

2.2.1 / 2014-12-30
==================

* Have snapshot builds upload to oss.jfrog.org instead of standard bintray
* Throw build exception if users try to use devSnapshot
* Move to nebula.nebula-plugin 2.0.2

2.2.0 / 2014-12-18
==================

* initial release
