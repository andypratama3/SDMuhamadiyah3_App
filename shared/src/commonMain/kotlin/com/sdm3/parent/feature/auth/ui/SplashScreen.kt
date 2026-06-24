package com.sdm3.parent.feature.auth.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sdm3.parent.core.designsystem.component.Sdm3Logo
import com.sdm3.parent.core.designsystem.theme.*
import com.sdm3.parent.core.navigation.SDM3Route
import com.sdm3.parent.core.security.SecureTokenManager
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import sdmuhammadiyah3samarinda.shared.generated.resources.Res
import sdmuhammadiyah3samarinda.shared.generated.resources.app_name
import sdmuhammadiyah3samarinda.shared.generated.resources.splash_subtitle

@Composable
fun SplashScreen(
    onNavigate: (SDM3Route) -> Unit,
    secureTokenManager: SecureTokenManager? = if (LocalInspectionMode.current) null else koinInject()
) {
    SplashContent(
        onAnimationFinished = {
            val studentId = secureTokenManager?.getSelectedStudentId()
            if (studentId != null) {
                onNavigate(SDM3Route.Main(studentId))
            } else {
                onNavigate(SDM3Route.Onboarding)
            }
        }
    )
}

@Composable
private fun SplashContent(
    onAnimationFinished: suspend () -> Unit = {},
    logoSize: Dp = 160.dp
) {
    val isPreview = LocalInspectionMode.current
    val reducedMotion = LocalReducedMotion.current
    var startAnimation by remember { mutableStateOf(isPreview) }

    // High-End Vanguard Motion Curves
    val easeOutQuart = CubicBezierEasing(0.25f, 1f, 0.5f, 1f)
    val easeOutBack = CubicBezierEasing(0.34f, 1.56f, 0.64f, 1f)
    val easeOutCirc = CubicBezierEasing(0.0f, 0.55f, 0.45f, 1f)

    // Dynamic Mesh Animators
    val infiniteTransition = rememberInfiniteTransition(label = "mesh_transition")
    val phase1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing), RepeatMode.Reverse),
        label = "mesh_phase_1"
    )
    val phase2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(11000, easing = LinearEasing), RepeatMode.Reverse),
        label = "mesh_phase_2"
    )

    // State Animations
    val scaleAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.6f,
        animationSpec = if (reducedMotion) snap() else tween(1400, easing = easeOutBack),
        label = "scale"
    )

    val rotationAnim by animateFloatAsState(
        targetValue = if (startAnimation) 0f else -10f,
        animationSpec = if (reducedMotion) snap() else tween(1400, easing = easeOutBack),
        label = "rotation"
    )

    val logoAlphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = if (reducedMotion) snap() else tween(1000, easing = easeOutQuart),
        label = "logo_alpha"
    )

    // Memberikan sedikit delay untuk teks agar muncul setelah logo mulai membesar
    var textStartAnimation by remember { mutableStateOf(isPreview) }
    val textAlphaAnim by animateFloatAsState(
        targetValue = if (textStartAnimation) 1f else 0f,
        animationSpec = if (reducedMotion) snap() else tween(1000, easing = easeOutQuart),
        label = "text_alpha"
    )

    val blurAnim by animateDpAsState(
        targetValue = if (startAnimation) 0.dp else 24.dp,
        animationSpec = if (reducedMotion) snap() else tween(1500, easing = easeOutQuart),
        label = "blur"
    )

    val progressAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = if (reducedMotion) snap() else tween(2800, easing = easeOutCirc),
        label = "progress"
    )

    LaunchedEffect(Unit) {
        delay(300)
        startAnimation = true
        delay(400) // Delay sebelum teks muncul
        textStartAnimation = true
        delay(2500)
        onAnimationFinished()
    }

    // Menggunakan Deep Forest Background agar selaras dengan identitas Primary (Hijau)
    val deepBrandBackground = Color(0xFF06140A)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(deepBrandBackground),
        contentAlignment = Alignment.Center
    ) {
        // Multi-Orb Animated Mesh Background
        Canvas(modifier = Modifier.fillMaxSize().alpha(if (startAnimation) 1f else 0f)) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val maxRadius = canvasWidth * 1.2f

            // Orb 1: Primary Color (Hijau) bergerak horizontal & sedikit vertikal
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Primary.copy(alpha = 0.25f), Color.Transparent),
                    center = Offset(canvasWidth * phase1, canvasHeight * 0.3f),
                    radius = maxRadius
                ),
                radius = maxRadius,
                center = Offset(canvasWidth * phase1, canvasHeight * 0.3f)
            )

            // Orb 2: Secondary Color (Emas/Oranye) untuk aksen kontras yang elegan
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Secondary.copy(alpha = 0.15f), Color.Transparent),
                    center = Offset(canvasWidth * (1f - phase2), canvasHeight * 0.7f),
                    radius = maxRadius * 0.8f
                ),
                radius = maxRadius * 0.8f,
                center = Offset(canvasWidth * (1f - phase2), canvasHeight * 0.7f)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = Spacing.xxl)
        ) {
            // Logo with 3D-like Tilt, Bloom & Shimmer
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = scaleAnim
                        scaleY = scaleAnim
                        rotationZ = rotationAnim
                        alpha = logoAlphaAnim
                    }
                    .blur(blurAnim),
                contentAlignment = Alignment.Center
            ) {
                // Outer Bloom membesar lebih dinamis
                Box(
                    modifier = Modifier
                        .size(logoSize * 2f)
                        .background(
                            Brush.radialGradient(
                                listOf(Primary.copy(alpha = 0.2f * logoAlphaAnim), Color.Transparent)
                            )
                        )
                )

                Sdm3Logo(
                    size = logoSize,
                    showBackground = false
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xxxl))

            // Typography Stack with Staggered Reveal
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.graphicsLayer {
                    alpha = textAlphaAnim
                    translationY = (1f - textAlphaAnim) * 30f // Slide up halus
                }
            ) {
                Text(
                    text = stringResource(Res.string.app_name),
                    color = Color.White,
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-1.0).sp,
                        lineHeight = 40.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(0.9f)
                )

                Spacer(modifier = Modifier.height(Spacing.md))

                Text(
                    text = stringResource(Res.string.splash_subtitle).uppercase(),
                    color = Secondary.copy(alpha = 0.8f), // Menggunakan Secondary untuk aksen subtitle
                    style = MaterialTheme.typography.labelLarge.copy(
                        letterSpacing = 5.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Micro-Precision Progress Line (Center to Edge Expansion)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 90.dp)
                .width(140.dp)
                .height(2.dp)
                .clip(RoundedCornerShape(1.dp))
                .background(Color.White.copy(alpha = 0.05f)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progressAnim)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color.Transparent, Primary, Color.Transparent)
                        )
                    )
            )
        }

        // Institutional Footer
        Text(
            text = "SISTEM INFORMASI MANAJEMEN TERPADU",
            color = Color.White.copy(alpha = 0.3f),
            style = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Normal
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = Spacing.xl)
                .graphicsLayer { alpha = textAlphaAnim }
        )
    }
}