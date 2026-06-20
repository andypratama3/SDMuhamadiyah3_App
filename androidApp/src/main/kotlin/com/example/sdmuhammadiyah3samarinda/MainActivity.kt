package com.example.sdmuhammadiyah3samarinda

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sdm3.parent.App
import com.sdm3.parent.core.di.allAppModules
import org.koin.android.ext.koin.androidContext
import org.koin.compose.KoinApplication
import org.koin.compose.koinConfiguration

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            KoinApplication(
                config = koinConfiguration {
                    androidContext(this@MainActivity)
                    modules(allAppModules)
                }
            ) {
                App()
            }
        }
    }
}
