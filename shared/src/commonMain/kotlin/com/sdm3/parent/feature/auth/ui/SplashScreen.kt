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
import com.sdm3.parent.core.designsystem.theme.PrimaryContainer
import com.sdm3.parent.core.designsystem.theme.Spacing
import com.sdm3.parent.core.designsystem.theme.TextPrimary
import com.sdm3.parent.core.designsystem.theme.TextSecondary
import com.sdm3.parent.core.designsystem.theme.TextTertiary
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
        targetValue = if (startAnimation) 1f else 0.85f,
        animationSpec = tween(800, easing = EaseOutBack),
        label = "scale"
    )
    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(600, easing = EaseInOutQuart),
        label = "alpha"
    )
    val subtitleAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(800, delayMillis = 300, easing = EaseInOutQuart),
        label = "subtitleAlpha"
    )

    LaunchedEffect(Unit) {
        delay(100)
        startAnimation = true
        delay(2000)
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
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(400.dp)
                .graphicsLayer {
                    alpha = 0.06f * alphaAnim
                    scaleX = 1.2f
                    scaleY = 1.2f
                }
                .background(
                    Brush.radialGradient(listOf(Primary, Color.Transparent)),
                    RoundedCornerShape(200.dp)
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = Spacing.xl)
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .graphicsLayer {
                        scaleX = scaleAnim
                        scaleY = scaleAnim
                        alpha = alphaAnim
                    }
                    .clip(RoundedCornerShape(60.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.White,
                                PrimaryContainer
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Sdm3Logo(
                    size = 80.dp,
                    modifier = Modifier
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.graphicsLayer {
                    alpha = subtitleAlpha
                    translationY = (1f - subtitleAlpha) * 20f
                }
            ) {
                Text(
                    text = "SDM3 Parent Portal",
                    color = TextPrimary,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(Spacing.xs))

                Text(
                    text = "Portal Orang Tua",
                    color = TextSecondary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(Spacing.xl))

                Text(
                    text = "v1.0.0",
                    color = TextTertiary,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


