# Plotline Integration Guide (React + Android)

This document provides a clear, step-by-step guide to integrating **Plotline Engage** inside a React layer and an Android host application.

---

## ✅ React Layer Setup

### 1. Install Plotline

Run the following command inside your React project:

```bash
npm i plotline-engage
```

---

## ✅ Android Layer Setup

Below are the required updates across various Gradle files and the Application class.

---

## 1. Project Level `build.gradle`

Add the following dependency resolution rules:

```kotlin
subprojects {
    configurations.all {
        resolutionStrategy.eachDependency {
            if (requested.group == "com.facebook.react" && requested.name == "react-native") {
                useTarget("com.facebook.react:react-android:0.73.6")
                because("React Native Maven artifact renamed from react-native -> react-android")
            }

            if (requested.group == "so.plotline" && requested.name == "plotline-android-sdk") {
                useTarget("com.gitlab.plotline:plotline-android-sdk:4.4.6")
                because("Plotline SDK Maven artifact downloaded from JitPack")
            }
        }
    }
}

subprojects {
    val isPlotlineModule = (path == ":plotline-engage") || projectDir.path.contains("node_modules/plotline-engage/android")

    if (isPlotlineModule) {
        plugins.withId("com.android.library") {
            extensions.findByType(LibraryExtension::class.java)?.let { androidExt ->
                if (androidExt.namespace.isNullOrBlank()) {
                    androidExt.namespace = "com.reactnativeplotline"
                }
            }
        }
    }
}
```

---

## 2. App Level `build.gradle`

Add the following code at the top level of your **app module** `build.gradle.kts`:

```kotlin
val nodeBinary: String = providers.environmentVariable("NODE_BINARY")
    .orElse("/opt/homebrew/bin/node") // <-- Change if Node is elsewhere
    .get()

val bundleMiniAppJs = tasks.register("bundleMiniAppJs") {
    group = "react"
    description = "Bundle JS from the MiniApp repo into host app assets"

    doLast {
        val miniAppDir = rootProject.file("../rnUiApp")
        val outDir = file("$projectDir/src/main/assets")

        outDir.mkdirs()

        file("${outDir}/index.android.bundle").delete()
        fileTree(outDir).matching {
            include("index.android.bundle", "drawable-*/**", "*/drawable-*/**")
        }.files.forEach { it.delete() }

        exec {
            workingDir(miniAppDir)
            commandLine(
                nodeBinary,
                "node_modules/react-native/cli.js",
                "bundle",
                "--platform", "android",
                "--dev", "false",
                "--entry-file", "index.js",
                "--bundle-output", File(outDir, "index.android.bundle").absolutePath,
                "--assets-dest", outDir.absolutePath
            )
        }
    }
}

tasks.matching { it.name == "mergeReleaseAssets" || it.name == "mergeReleaseResources" }
    .configureEach {
        dependsOn(bundleMiniAppJs)
    }

tasks.matching { it.name == "mergeDebugAssets" || it.name == "mergeDebugResources" }
    .configureEach {
        dependsOn(bundleMiniAppJs)
    }
```

### Add Plotline to Dependencies

Inside the same module's `dependencies` block:

```kotlin
dependencies {
    implementation(project(":plotline-engage"))
}
```

---

## 3. `settings.gradle`

### Add JitPack Repository

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}
```

### Include Plotline Module

```kotlin
include(":plotline-engage")
project(":plotline-engage").projectDir = file("../rnUiApp/node_modules/plotline-engage/android")
```

---

## 4. Application Class Setup

### Import Plotline Package

```kotlin
import com.reactnativeplotline.RNPlotlinePackage
```

### Add the Package to the RN Package List

```kotlin
val packages: List<ReactPackage> = listOf(
    MainReactPackage(),
    RNPlotlinePackage() // ✅ Add Plotline here
)
```

### Include Packages in the React Instance Manager

```kotlin
reactInstanceManager = ReactInstanceManager.builder()
    .setApplication(this)
    .setCurrentActivity(null)
    .addPackages(packages)
```

---
