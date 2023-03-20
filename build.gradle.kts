plugins {
    kotlin("jvm") version "1.8.10"
    kotlin("plugin.serialization") version "1.8.10"
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
}

val projectName: String by project
val projectVersion: String by project
val projectGroup: String by project

group = projectGroup
version = projectVersion

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Jupiter's JUNIT
    // TODO: write unit tests
    testImplementation(libs.junitApi)
    testRuntimeOnly(libs.junitEngine)

    // OW2 ASM
    implementation(libs.asm)
    implementation(libs.asmCommons)

    // Kotlinx.serialization JSON library
    implementation(libs.kxSerJSON)
}

gradlePlugin {
    plugins {
        create("weave") {
            // Using jitpack.io for the time being
            id = "com.github.weave-mc.weave"
            displayName = "Weave Plugin"
            description =
                "Implements Remapped Minecraft libraries and Weave-Loader intended for developing Minecraft Mods"
            implementationClass = "${group}.WeavePlugin"
        }
    }
}

// Use Gradle Plugin Portal later on when the plugin is finished
// val publishProps = Properties()
// file("gradle-publish.properties").inputStream().use { publishProps.load(it) }
//
// publishing {
//     repositories {
//         maven {
//             url = uri("https://plugins.gradle.org/m2/")
//             credentials {
//                 username = publishProps.getProperty("publishKey")
//                 password = publishProps.getProperty("publishSecret")
//             }
//         }
//     }
// }
//
// tasks.getByName<Test>("test") {
//     useJUnitPlatform()
// }
