<img
    align="right" width="200px"
    src="https://static.wikia.nocookie.net/minecraft_gamepedia/images/d/d7/Loom_%28S%29_JE1_BE1.png/revision/latest?cb=20210116072516"
/>

### Weave-ified Loom

<img
    align="left" alt="status"
    src="https://github.com/Weave-MC/Weave/actions/workflows/gradle.yml/badge.svg"
/>

---

A Gradle build system plugin used to automate the setup of a modding environment.

### Getting Started

- Make sure a [JDK 17][jdk] is installed on your system.

- To test (Windows and Unix)

```bash
java --version # should output some JDK 17.
```

### Installation

#### Building with Gradle

- `git clone` the project, this can be achieved by installing [git][git], then running

```bash
git clone https://github.com/Weave-MC/Weave-Loader.git "Weave-Loader"
```

- **UN*X**

```bash
cd $_ ; chmod +x ./gradlew && ./gradlew build
```

> Note that `$_` is the last argument from the previous command, should be run after cloning.

- **Windows**

```powershell
cd Weave-Loader ; .\gradlew.bat build
```

### Usage

You can use Weave-Loader as a dependency in your project by implementing it as a repository. To do this, add the
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
    id "com.github.weave-mc.weave" version "ec59a8bceb"
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
    id("com.github.weave-mc.weave") version ("ec59a8bceb")
}
```

> Replace `${VERSION}` with the version of Weave-Loader in the Build Reference. (soon:tm:)

---

<div align="right">

Weave is licensed under the [GNU General Public License Version 3][license].

</div>

[git]:     https://git-scm.com/
[jdk]:     https://www.azul.com/downloads/?version=java-17-lts&package=jdk
[license]: https://github.com/Weave-MC/Weave-Loader/blob/main/LICENSE
