package com.sdm3.parent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sdm3.parent.App
import com.sdm3.parent.core.di.allAppModules
import org.koin.android.ext.koin.androidContext
import org.koin.compose.KoinApplication

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

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
}
