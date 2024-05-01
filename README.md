# Weave's Gradle Plugin

<img
    align="right" width="200px"
    src="https://raw.githubusercontent.com/Weave-MC/.github/master/assets/icon.png"
/>

<img
    align="left" alt="status"
    src="https://github.com/Weave-MC/Weave/actions/workflows/gradle.yml/badge.svg"
/>

---

A Gradle build system plugin used to automate the setup of a Weave modding environment.

### Usage

You can use Weave-Gradle by adding our maven repository to your **settings** buildscripts:

<details open>
<summary>Kotlin DSL (settings.gradle.kts)</summary>

```gradle
pluginManagement {
    repositories {
        maven("https://repo.weavemc.dev/releases")
    }
}
```
</details>

<details>
<summary>Groovy DSL (settings.gradle)</summary>

```gradle
pluginManagement {
    repositories {
        maven {
            url = "https://repo.weavemc.dev/releases"
        }
    }
}
```
</details>

Now that you've added our repository, you can apply the plugin in your `build.gradle(.kts)` file's `plugins` block like so:
```kotlin
plugins {
    id("net.weavemc.gradle") version "1.0.0-PRE"
}
```

---

<div align="right">

Weave-Gradle is licensed under the [GNU General Public License Version 3][license].

</div>

[git]:     https://git-scm.com/
[jdk]:     https://www.azul.com/downloads/?version=java-17-lts&package=jdk
[license]: ./LICENSE
