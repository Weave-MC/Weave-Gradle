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

<<<<<<< HEAD
dependencies {
    implementation("org.ow2.asm:asm:9.2")
    implementation("org.ow2.asm:asm-tree:9.2")
    implementation("org.ow2.asm:asm-commons:9.2")
    compileOnly("org.projectlombok:lombok:1.18.22")
    annotationProcessor("org.projectlombok:lombok:1.18.22")
=======
gradlePlugin {
    /* We gotta figrue this out */
    val loom by plugins.creating {
        id = "$group"
        implementationClass = "${group}.WeavePlugin"
    }
>>>>>>> 9b48e6917f966b8c49d6682bcc212e87ff3b20c1
}
