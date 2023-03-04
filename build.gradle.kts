plugins {
    idea
    java
    `java-gradle-plugin`
    `maven-publish`
}

val projectName:    String by project
val projectVersion: String by project
val projectGroup:   String by project

group   = projectGroup
version = projectVersion

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")

    implementation("org.ow2.asm:asm:9.2")
    implementation("org.ow2.asm:asm-tree:9.2")
    implementation("org.ow2.asm:asm-commons:9.2")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("commons-io:commons-io:2.11.0")
    implementation("com.google.code.gson:gson:2.10.1")

    compileOnly("org.projectlombok:lombok:1.18.22")
    annotationProcessor("org.projectlombok:lombok:1.18.22")
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
