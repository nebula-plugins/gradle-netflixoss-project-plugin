import nebula.plugin.contacts.Contact

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
plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `groovy`
    id("nebula.plugin-plugin") version "14.2.1"
}

description = "Template for NetflixOSS projects"


val rspieldenner = Contact("rob.spieldenner@gmail.com")
rspieldenner.moniker = "Rob Spieldenner"
rspieldenner.github = "rspieldenner"
val rpalcolea = Contact("roberto@perezalcolea.info")
rpalcolea.moniker = "Roberto Perez Alcolea"
rpalcolea.github = "rpalcolea"
contacts {
    people.set("rob.spieldenner@gmail.com", rspieldenner)
    people.set("roberto@perezalcolea.info", rpalcolea)
}

dependencies {
    implementation("com.netflix.nebula:gradle-contacts-plugin:latest.release")
    implementation("com.netflix.nebula:gradle-dependency-lock-plugin:latest.release")
    implementation("com.netflix.nebula:nebula-bintray-plugin:latest.release")
    implementation("com.netflix.nebula:gradle-info-plugin:latest.release")
    implementation("com.netflix.nebula:gradle-info-plugin:latest.release")
    implementation("com.netflix.nebula:nebula-core:latest.release")
    implementation("com.netflix.nebula:nebula-project-plugin:latest.release")
    implementation("com.netflix.nebula:nebula-publishing-plugin:latest.release")
    implementation("com.netflix.nebula:nebula-release-plugin:latest.release")
    implementation("com.netflix.nebula:gradle-java-cross-compile-plugin:latest.release")
    implementation("gradle.plugin.nl.javadude.gradle.plugins:license-gradle-plugin:0.14.0")
    testImplementation("org.ajoberstar.grgit:grgit-core:3.1.1") {
        exclude(group = "org.codehaus.groovy", module = "groovy")
    }
}

pluginBundle {
    plugins {
        create("netflixoss") {
            id = "nebula.netflixoss"
            displayName = "Netflix OSS Project Template"
            description = project.description
            tags = listOf("nebula", "netflix", "oss")
        }
    }
}
