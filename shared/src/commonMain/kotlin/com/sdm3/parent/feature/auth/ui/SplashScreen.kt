package com.sdm3.parent.feature.auth.ui

import androidx.compose.animation.core.EaseInOutQuart
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sdm3.parent.core.designsystem.component.Sdm3Logo
import com.sdm3.parent.core.designsystem.theme.Primary
import com.sdm3.parent.core.designsystem.theme.Spacing
import com.sdm3.parent.core.navigation.SDM3Route
import com.sdm3.parent.core.security.SecureTokenManager
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

@Composable
fun SplashScreen(
    onNavigate: (SDM3Route) -> Unit,
    secureTokenManager: SecureTokenManager = koinInject()
) {
    var startAnimation by remember { mutableStateOf(false) }

    val scaleAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.3f,
        animationSpec = tween(1200, easing = EaseOutBack),
        label = "scale"
    )
    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(1000, easing = EaseInOutQuart),
        label = "alpha"
    )

    LaunchedEffect(Unit) {
        delay(200)
        startAnimation = true
        delay(1800)
        val token = secureTokenManager.getBearerToken()
        if (token != null) {
            val studentId = secureTokenManager.getSelectedStudentId()
            if (studentId != null) {
                onNavigate(SDM3Route.Main(studentId))
            } else {
                onNavigate(SDM3Route.PilihAnak)
            }
        } else {
            onNavigate(SDM3Route.Onboarding)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Primary, Primary.copy(alpha = 0.85f))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(400.dp)
                .graphicsLayer {
                    alpha = 0.05f * alphaAnim
                    scaleX = 1.2f
                    scaleY = 1.2f
                }
                .background(Brush.radialGradient(listOf(Color.White, Color.Transparent)), RoundedCornerShape(200.dp))
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = Spacing.xl)
        ) {
            Box(
                modifier = Modifier.size(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .graphicsLayer {
                            scaleX = alphaAnim * 0.5f + 0.5f
                            scaleY = alphaAnim * 0.5f + 0.5f
                            alpha = (1f - alphaAnim) * 0.3f
                        }
                        .clip(RoundedCornerShape(50.dp))
                        .background(Color.White)
                )

                Sdm3Logo(
                    size = 100.dp,
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX = scaleAnim
                            scaleY = scaleAnim
                            alpha = alphaAnim
                        }
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.graphicsLayer {
                    alpha = alphaAnim
                    translationY = (1f - alphaAnim) * 30f
                }
            ) {
                Text(
                    text = "SDM3 Parent Portal",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "SD Muhammadiyah 3 Samarinda",
                    color = Color.White.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
