<img
    align="right" width="200px"
    src="https://raw.githubusercontent.com/Weave-MC/.github/master/assets/icon.png"
/>

### Weave-ified Loom

<img
    align="left" alt="status"
    src="https://github.com/Weave-MC/Weave/actions/workflows/gradle.yml/badge.svg"
/>

---

A Gradle build system plugin used to automate the setup of a Weave modding environment and testing Minecraft.

### Usage

You can use Weave-Gradle as a plugin in your project by implementing it using JitPack. To do this, add the
following code to your `build.gradle` file.

- **Groovy DSL**

> `settings.gradle`

```gradle
pluginManagement {
    repositories {
        maven {
            name = 'JitPack'
            url = 'https://jitpack.io'
        }
    }
}
```

> `build.gradle`

```gradle
plugins {
    id "com.github.weave-mc.weave-gradle" version ${VERSION}
}
```

- **Kotlin DSL**

> `settins.gradle.kts`

```kt
pluginManagement {
    repositories {
        maven("https://jitpack.io")
    }
}
```

> `build.gradle.kts`

```kt
plugins {
    id("com.github.weave-mc.weave-gradle") version (${VERSION})
}
```

> Replace `${VERSION}` with the version of Weave-Gradle in the Build Reference. (soon:tm:)

---

<div align="right">

Weave is licensed under the [GNU General Public License Version 3][license].

</div>

[git]:     https://git-scm.com/
[jdk]:     https://www.azul.com/downloads/?version=java-17-lts&package=jdk
[license]: https://github.com/Weave-MC/Weave-Gradle/blob/main/LICENSE
