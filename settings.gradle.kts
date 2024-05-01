val projectName: String by settings

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.+"
}

rootProject.name = projectName
