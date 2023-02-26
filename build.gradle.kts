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

repositories.mavenCentral()

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")

    implementation("org.ow2.asm:asm:9.2")
    implementation("org.ow2.asm:asm-tree:9.2")
    implementation("org.ow2.asm:asm-commons:9.2")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("commons-io:commons-io:2.11.0")

    compileOnly("org.projectlombok:lombok:1.18.22")
    annotationProcessor("org.projectlombok:lombok:1.18.22")
}

gradlePlugin {
    plugins {
        create("loom") {
            id = "$group"
            implementationClass = "${group}.WeavePlugin"
        }
    }
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
