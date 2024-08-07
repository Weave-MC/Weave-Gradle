plugins {
    alias(libs.plugins.kotlin.serialization)
    `kotlin-dsl`
    `maven-publish`
}

val projectName: String by project
val projectVersion: String by project
val projectGroup: String by project

group = projectGroup
version = projectVersion

kotlin.jvmToolchain(8)

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.weavemc.dev/releases")
}

dependencies {
    implementation(libs.bundles.asm)
    implementation(libs.kxser.json)
    implementation(libs.mappings)
    implementation(libs.weave.internals)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>>()
    .configureEach {
        compilerOptions
            .languageVersion
            .set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_1_9)
    }

publishing {
    repositories {
        maven {
            name = "WeaveMC"
            url = uri("https://repo.weavemc.dev/releases")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
}

gradlePlugin {
    plugins {
        create("weave") {
            id = projectGroup
            displayName = projectName
            description =
                "Implements Remapped Minecraft libraries intended for developing Minecraft mods with Weave"
            implementationClass = "$projectGroup.WeaveGradle"
        }
    }
}
