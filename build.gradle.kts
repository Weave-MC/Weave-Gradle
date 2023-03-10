plugins {
    `java-gradle-plugin`
    `maven-publish`
}

val projectName: String by project
val projectVersion: String by project
val projectGroup: String by project

group = projectGroup
version = projectVersion

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Jupiter's JUNIT
    testImplementation(libs.junitapi)
    testRuntimeOnly(libs.junitengine)
    // OW2 ASM
    implementation(libs.asm)
    implementation(libs.asmcommons)
    // Google's GSON
    implementation(libs.gson)
    // Lombok
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

gradlePlugin {
    plugins {
        create("weave") {
            // Using jitpack.io for the time being
            id = "com.github.weave-mc.weave"
            displayName = "Weave Plugin"
            description = "Implements Remapped Minecraft libraries and Weave-Loader intended for developing Minecraft Mods"
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
