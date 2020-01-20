import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("com.github.jengelman.gradle.plugins:shadow:5.2.0")
    }
}

plugins {
    kotlin("jvm") version "1.3.41"
}

apply {
    plugin("com.github.johnrengelman.shadow")
}

val kafka_version = "2.4.0"
val ktor_version = "1.3.0"

group = "com.jcthenerd"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.apache.kafka:connect-api:$kafka_version")
    implementation("org.apache.kafka:kafka-clients:$kafka_version")
    implementation("io.ktor:ktor-client-apache:$ktor_version")
    implementation("io.ktor:ktor-client-json-jvm:$ktor_version")
    implementation("io.ktor:ktor-client-gson:$ktor_version")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    getByName<ShadowJar>("shadowJar") {
        dependencies {
            exclude(dependency("org.apache.kafka:connect-api:$kafka_version"))
            exclude(dependency("org.apache.kafka:kafka-clients:$kafka_version"))
            exclude(dependency("net.jpountz.lz4:.*:.*"))
            exclude(dependency("org.xerial.snappy:.*:.*"))
            exclude(dependency("org.slf4j:.*:.*"))
        }
    }
}