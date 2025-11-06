plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.hostapp"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.hostapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    sourceSets {
        getByName("main") {
            assets.srcDirs(files("$projectDir/src/main/assets"))
        }
    }

}

val nodeBinary: String = providers.environmentVariable("NODE_BINARY")
    .orElse("/opt/homebrew/bin/node") // <-- change this if your Node lives elsewhere
    .get()

val bundleMiniAppJs = tasks.register("bundleMiniAppJs") {
    group = "react"
    description = "Bundle JS from the MiniApp repo into host app assets"

    doLast {
        // Paths
        val miniAppDir = rootProject.file("../rnUiApp") // adjust if your RN repo path differs
        val outDir = file("$projectDir/src/main/assets")

        // Ensure assets folder exists
        outDir.mkdirs()

        // Clean old bundles/assets
        file("${outDir}/index.android.bundle").delete()
        fileTree(outDir).matching {
            include("index.android.bundle", "drawable-*/**", "*/drawable-*/**")
        }.files.forEach { it.delete() }

        // Execute React Native CLI bundler
        exec {
            workingDir(miniAppDir)
            commandLine(
                nodeBinary,
                "node_modules/react-native/cli.js",
                "bundle",
                "--platform", "android",
                "--dev", "false",
                "--entry-file", "index.js", // or "index.tsx" if using TS
                "--bundle-output", File(outDir, "index.android.bundle").absolutePath,
                "--assets-dest", outDir.absolutePath
            )
        }
    }
}

// Hook this task into the Android build process (for Release)
tasks.matching { it.name == "mergeReleaseAssets" || it.name == "mergeReleaseResources" }
    .configureEach {
        dependsOn(bundleMiniAppJs)
    }

// (Optional) If you also want to bundle for Debug builds:
tasks.matching { it.name == "mergeDebugAssets" || it.name == "mergeDebugResources" }
    .configureEach {
        dependsOn(bundleMiniAppJs)
    }


dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    val rn = "0.73.6"

    // Pull the full AARs with transitive deps
    implementation("com.facebook.react:react-android:$rn")
    implementation("com.facebook.react:hermes-android:$rn")

    implementation(project(":plotline-engage"))
}