package com.sdm3.parent

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sdm3.parent.core.designsystem.theme.SDM3Theme
import com.sdm3.parent.core.navigation.SDM3NavHost
import com.sdm3.parent.core.navigation.SDM3Route

@Composable
fun App() {
    SDM3Theme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            SDM3NavHost(
                startDestination = SDM3Route.Splash
            )
        }
    }
}
