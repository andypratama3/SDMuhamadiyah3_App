package com.sdm3.parent.feature.auth.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.isSystemInDarkTheme
import com.sdm3.parent.core.AppBranding
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sdm3.parent.core.designsystem.component.Sdm3Logo
import com.sdm3.parent.core.designsystem.theme.*
import com.sdm3.parent.core.network.ApiError
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.core.navigation.PostAuthNavigator
import com.sdm3.parent.core.navigation.SDM3Route
import com.sdm3.parent.core.security.SecureTokenManager
import com.sdm3.parent.domain.model.RoleContext
import com.sdm3.parent.domain.repository.AuthRepositoryContract
import kotlinx.coroutines.delay
import androidx.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import sdmuhammadiyah3samarinda.shared.generated.resources.Res
import sdmuhammadiyah3samarinda.shared.generated.resources.app_name
import sdmuhammadiyah3samarinda.shared.generated.resources.splash_subtitle

@Composable
fun SplashScreen(
    onNavigate: (SDM3Route) -> Unit,
    authRepository: AuthRepositoryContract? = if (LocalInspectionMode.current) null else koinInject(),
    secureTokenManager: SecureTokenManager? = if (LocalInspectionMode.current) null else koinInject()
) {
    SplashContent(
        onAnimationFinished = {
            val tokenManager = secureTokenManager
            val auth = authRepository
            val token = tokenManager?.getBearerToken()
            when {
                token.isNullOrBlank() -> {
                    if (tokenManager?.isOnboardingCompleted() == true) {
                        onNavigate(SDM3Route.Login)
                    } else {
                        onNavigate(SDM3Route.Onboarding)
                    }
                }
                auth != null -> {
                    when (val result = auth.getAuthenticatedUser()) {
                        is ApiResult.Success -> {
                            onNavigate(
                                PostAuthNavigator.resolveRoute(
                                    roleContext = RoleContext.fromUser(result.data),
                                    selectedStudentId = tokenManager.getSelectedStudentId(),
                                    onboardingCompleted = tokenManager.isOnboardingCompleted(),
                                )
                            )
                        }
                        is ApiResult.Error -> {
                            when (result.error) {
                                is ApiError.Unauthorized,
                                is ApiError.SessionExpired,
                                is ApiError.Forbidden -> {
                                    auth.clearLocalSession()
                                    onNavigate(
                                        if (tokenManager.isOnboardingCompleted()) SDM3Route.Login
                                        else SDM3Route.Onboarding
                                    )
                                }
                                is ApiError.NoInternet,
                                is ApiError.Timeout -> {
                                    onNavigate(
                                        PostAuthNavigator.resolveRoute(
                                            roleContext = auth.resolveStoredRoleContext(),
                                            selectedStudentId = tokenManager.getSelectedStudentId(),
                                            onboardingCompleted = tokenManager.isOnboardingCompleted(),
                                        )
                                    )
                                }
                                else -> {
                                    onNavigate(
                                        PostAuthNavigator.resolveRoute(
                                            roleContext = auth.resolveStoredRoleContext(),
                                            selectedStudentId = tokenManager.getSelectedStudentId(),
                                            onboardingCompleted = tokenManager.isOnboardingCompleted(),
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
                else -> onNavigate(SDM3Route.Login)
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
    val isDark = isSystemInDarkTheme()
    val colorScheme = MaterialTheme.colorScheme
    val heroContent = heroContentColor()
    val glassSurface = glassSurfaceColor()
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

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(splashBackgroundBrush())
            .safeDrawingPadding(),
        contentAlignment = Alignment.Center
    ) {
        val isCompact = maxHeight < 700.dp
        val titleStyle = if (isCompact) {
            MaterialTheme.typography.headlineLarge
        } else {
            MaterialTheme.typography.displayMedium
        }
        // Multi-Orb Animated Mesh Background (Modern Style)
        Canvas(modifier = Modifier.fillMaxSize().alpha(if (startAnimation) 1f else 0f)) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val maxRadius = canvasWidth * 1.8f

            // Orb 1: Primary Blue Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        colorScheme.primary.copy(alpha = if (isDark) 0.25f else 0.2f),
                        Color.Transparent,
                    ),
                    center = Offset(canvasWidth * phase1, canvasHeight * 0.15f),
                    radius = maxRadius
                ),
                radius = maxRadius,
                center = Offset(canvasWidth * phase1, canvasHeight * 0.15f)
            )

            // Orb 2: Secondary Green Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        colorScheme.secondary.copy(alpha = if (isDark) 0.2f else 0.15f),
                        Color.Transparent,
                    ),
                    center = Offset(canvasWidth * (1f - phase2), canvasHeight * 0.85f),
                    radius = maxRadius * 0.8f
                ),
                radius = maxRadius * 0.8f,
                center = Offset(canvasWidth * (1f - phase2), canvasHeight * 0.85f)
            )

            // Orb 3: Tertiary Purple Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        colorScheme.tertiary.copy(alpha = if (isDark) 0.15f else 0.12f),
                        Color.Transparent,
                    ),
                    center = Offset(canvasWidth * 0.5f, canvasHeight * 0.5f),
                    radius = maxRadius * 0.5f
                ),
                radius = maxRadius * 0.5f,
                center = Offset(canvasWidth * 0.5f, canvasHeight * 0.5f)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-36).dp)
                .padding(horizontal = Spacing.xxl)
        ) {
            // Logo with Glassmorphic Glow
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
                // Glassmorphic Outer Bloom
                Box(
                    modifier = Modifier
                        .size(logoSize * 2.0f)
                        .background(
                            Brush.radialGradient(
                                listOf(glassSurface.copy(alpha = 0.15f * logoAlphaAnim), Color.Transparent)
                            )
                        )
                )

                Surface(
                    modifier = Modifier.size(logoSize),
                    shape = RoundedCornerShape(32.dp),
                    color = glassSurface.copy(alpha = 0.3f),
                    border = BorderStroke(2.dp, glassBorderColor()),
                    shadowElevation = 12.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Sdm3Logo(
                            size = logoSize * 0.65f,
                            showBackground = false
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            // Typography Stack with Modern Hierarchy
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.graphicsLayer {
                    alpha = textAlphaAnim
                    translationY = (1f - textAlphaAnim) * 24f
                }
            ) {
                Text(
                    text = stringResource(Res.string.app_name),
                    color = heroContent,
                    style = titleStyle.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.height(Spacing.md))

                // Modern Secondary Subtitle
                Surface(
                    color = colorScheme.secondaryContainer.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(999.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.splash_subtitle).uppercase(),
                        color = colorScheme.secondary,
                        style = MaterialTheme.typography.labelMedium.copy(
                            letterSpacing = 3.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .width(180.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        if (isDark) {
                            Color.White.copy(alpha = 0.1f)
                        } else {
                            glassSurface.copy(alpha = 0.15f)
                        },
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progressAnim)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color.Transparent, colorScheme.secondary, Color.Transparent)
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = AppBranding.SCHOOL_NAME.uppercase(),
                color = heroContent.copy(alpha = if (isDark) 0.55f else 0.4f),
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = if (isCompact) 1.sp else 2.sp,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .graphicsLayer { alpha = textAlphaAnim }
                    .padding(horizontal = Spacing.md),
            )
        }
    }
}

@Preview
@Composable
private fun SplashContentLightPreview() {
    SDM3Theme(darkTheme = false) {
        SplashContent(onAnimationFinished = {})
    }
}

@Preview
@Composable
private fun SplashContentDarkPreview() {
    SDM3Theme(darkTheme = true) {
        SplashContent(onAnimationFinished = {})
    }
}