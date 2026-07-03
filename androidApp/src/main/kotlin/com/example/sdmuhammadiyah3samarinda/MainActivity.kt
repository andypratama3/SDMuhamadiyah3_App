package com.sdm3.parent

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.sdm3.parent.core.di.allAppModules
import com.sdm3.parent.core.notification.PushNotificationDisplay
import com.sdm3.parent.platform.AndroidPlatformBootstrap
import com.sdm3.parent.platform.installAndroidPlatformBindings
import org.koin.android.ext.koin.androidContext
import org.koin.compose.KoinApplication

class MainActivity : AppCompatActivity() {

    private var platformBootstrap: AndroidPlatformBootstrap? = null

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* granted or denied — FCM registration handles missing token gracefully */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        PushNotificationDisplay.ensureChannel(this)
        requestNotificationPermissionIfNeeded()
        platformBootstrap = installAndroidPlatformBindings()

        setContent {
            @Suppress("DEPRECATION")
            KoinApplication(application = {
                androidContext(this@MainActivity)
                modules(allAppModules)
            }) {
                App()
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    override fun onDestroy() {
        platformBootstrap?.clear()
        platformBootstrap = null
        super.onDestroy()
    }
}
