package com.example.hostapp

import android.app.Application
import com.facebook.react.BuildConfig
import com.facebook.react.ReactInstanceManager
import com.facebook.react.ReactNativeHost
import com.facebook.react.ReactPackage
import com.facebook.react.common.LifecycleState
import com.facebook.react.shell.MainReactPackage
import com.facebook.soloader.SoLoader
import com.reactnativeplotline.RNPlotlinePackage

class CustomApplication : Application() {

    // Build the RN instance manager here so activities can reuse it
    lateinit var reactInstanceManager: ReactInstanceManager
        private set

    override fun onCreate() {
        super.onCreate()
        SoLoader.init(this, false)

        val packages: List<ReactPackage> = listOf(
            MainReactPackage(),
            // include your library's package if you exposed one (optional)
            RNPlotlinePackage()
        )

        reactInstanceManager = ReactInstanceManager.builder()
            .setApplication(this)
            .setCurrentActivity(null)
            .setJSMainModulePath("index") // used in dev
            .addPackages(packages)
            .setUseDeveloperSupport(BuildConfig.DEBUG)
            // In release, load from the AAR's bundled assets (index.android.bundle)
            .setBundleAssetName("index.android.bundle")
            .setInitialLifecycleState(LifecycleState.BEFORE_CREATE)
            .build()
    }
}
