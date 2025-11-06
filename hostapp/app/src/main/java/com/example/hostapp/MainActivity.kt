package com.example.hostapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.facebook.react.ReactFragment
import com.facebook.react.ReactRootView
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler

class MainActivity : ComponentActivity(), DefaultHardwareBackBtnHandler {
    private lateinit var reactRootView: ReactRootView

    private val reactInstanceManager
        get() = (application as CustomApplication).reactInstanceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        reactRootView = ReactRootView(this)
        // "RnUiApp" must match AppRegistry.registerComponent(...) in Repo A
        reactRootView.startReactApplication(reactInstanceManager, "rnUiApp", null)
        setContentView(reactRootView)
    }

    override fun onResume() {
        super.onResume()
        reactInstanceManager.onHostResume(this, this)
    }

    override fun onPause() {
        super.onPause()
        reactInstanceManager.onHostPause(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        reactInstanceManager.onHostDestroy(this)
    }

    override fun invokeDefaultOnBackPressed() {
        onBackPressedDispatcher.onBackPressed()
    }
}