/*
 * Copyright 2014-2021 Netflix, Inc.
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

import nebula.plugin.publishing.NebulaOssPublishingPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository

class PublishingPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.plugins.apply org.gradle.api.publish.plugins.PublishingPlugin
        project.plugins.apply NebulaOssPublishingPlugin

        project.afterEvaluate {
            project.tasks.withType(PublishToMavenRepository).configureEach {
                it.mustRunAfter(project.rootProject.tasks.named('release'))
            }

            project.rootProject.tasks.named('postRelease').configure {
                it.dependsOn(project.tasks.withType(PublishToMavenRepository))
            }
        }
    }
}
