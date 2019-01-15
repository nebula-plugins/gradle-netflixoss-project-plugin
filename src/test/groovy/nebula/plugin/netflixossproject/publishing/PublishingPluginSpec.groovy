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
package nebula.plugin.netflixossproject.publishing

import nebula.test.ProjectSpec
import spock.lang.Unroll

class PublishingPluginSpec extends ProjectSpec {
  def 'applying does not throw exceptions'() {
    when:
    project.plugins.apply PublishingPlugin

    then:
    noExceptionThrown()
  }

  @Unroll
  void 'should get URL from origin'() {
    when:
    def result = PublishingPlugin.calculateUrlFromOrigin(input)

    then:
    result == output

    where:
    input                                          || output
    'git@github.com:reactivex/rxjava-core.git'     || 'https://github.com/reactivex/rxjava-core'
    'https://github.com/reactivex/rxjava-core'     || 'https://github.com/reactivex/rxjava-core'
    'https://github.com/reactivex/rxjava-core.git' || 'https://github.com/reactivex/rxjava-core'
  }

  @Unroll
  void 'should get Repo from origin'() {
    when:
    def result = PublishingPlugin.calculateRepoFromOrigin(input)

    then:
    result == output

    where:
    input                                          || output
    'git@github.com:reactivex/rxjava-core.git'     || 'rxjava-core'
    'https://github.com/reactivex/rxjava-core'     || 'rxjava-core'
    'https://github.com/reactivex/rxjava-core.git' || 'rxjava-core'

  }
}
