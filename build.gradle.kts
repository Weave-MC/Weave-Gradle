plugins {
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
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    testImplementation(platform("org.junit:junit-bom:5.9.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
//    testImplementation(libs.junitApi) // version 5.8.1
//    testRuntimeOnly(libs.junitEngine) // version 5.8.1

    // OW2 ASM
    implementation(libs.asm) // version 9.4
    implementation(libs.asmCommons) // version 9.4

    // Kotlinx.serialization JSON library
    implementation(libs.kxSerJSON) // version 1.5.0
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

gradlePlugin {
    plugins {
        create("weave") {
            // Using jitpack.io for the time being
            id = "com.github.weave-mc.weave-gradle"
            displayName = "Weave Plugin"
            description =
                "Implements Remapped Minecraft libraries intended for developing Weave Mods"
            implementationClass = "net.weavemc.gradle.WeavePlugin"
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
