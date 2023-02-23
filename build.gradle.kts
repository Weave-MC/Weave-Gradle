plugins {
    idea
    java
    `java-gradle-plugin`
}

val projectName:    String by project
val projectVersion: String by project
val projectGroup:   String by project

group   = projectGroup
version = projectVersion

repositories.mavenCentral()

dependencies

gradlePlugin {
    /* We gotta figrue this out */
    val loom by plugins.creating {
        id = "$group"
        implementationClass = "${group}.WeavePlugin"
    }
}
