plugins {
    kotlin("plugin.serialization") version "1.8.10"
    `kotlin-dsl`
    `maven-publish`
}

val projectName: String by project
val projectVersion: String by project
val projectGroup: String by project

group = projectGroup
version = projectVersion

repositories {
    mavenCentral()
}

dependencies {
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
            id = "com.github.weave-mc.weave-gradle"
            displayName = "Weave-Gradle"
            description =
                "Implements Remapped Minecraft libraries intended for developing Minecraft mods with Weave"
            implementationClass = "${group}.WeaveGradle"
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>>()
    .configureEach {
        compilerOptions
            .languageVersion
            .set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_1_9)
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
