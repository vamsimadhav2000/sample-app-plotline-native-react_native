import com.android.build.api.dsl.LibraryExtension

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}

subprojects {
    configurations.all {
        resolutionStrategy.eachDependency {
            if (requested.group == "com.facebook.react" && requested.name == "react-native") {
                // Pin to the RN version you are using in the host
                useTarget("com.facebook.react:react-android:0.73.6")
                because("React Native Maven artifact renamed from react-native -> react-android")
            }

            if (requested.group == "so.plotline" && requested.name == "plotline-android-sdk") {
                //for using jitpack we are adding this setting.gradle line 22
                useTarget("com.gitlab.plotline:plotline-android-sdk:4.4.6")
                because("Plotline SDK  Maven artifact download from jitpack")
            }
        }
    }
}

subprojects {
    val isPlotlineModule = (path == ":plotline-engage") || projectDir.path.contains("node_modules/plotline-engage/android")

    if (isPlotlineModule) {
        plugins.withId("com.android.library") {
            extensions.findByType(LibraryExtension::class.java)?.let { androidExt ->
                // set only if missing to avoid fighting upstream changes
                if (androidExt.namespace.isNullOrBlank()) {
                    androidExt.namespace = "com.reactnativeplotline"
                }
            }
        }
    }
}
