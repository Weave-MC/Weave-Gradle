plugins {
    idea
    java
}

val projectName:    String by project
val projectVersion: String by project
val projectGroup:   String by project

group = projectGroup
version = projectVersion

repositories {
    mavenCentral()
}

dependencies {
}