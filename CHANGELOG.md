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

