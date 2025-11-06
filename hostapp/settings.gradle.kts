pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        //directly maven works
//        maven(url = "https://android-sdk.plotline.so")

        //for using jitpack we are adding this
        maven(url = "https://jitpack.io")
    }
}

rootProject.name = "hostapp"
include(":app")
// settings.gradle.kts (host app)
include(":plotline-engage")
project(":plotline-engage").projectDir = file("../rnUiApp/node_modules/plotline-engage/android")

