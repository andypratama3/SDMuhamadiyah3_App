package com.sdm3.parent.feature.auth.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sdm3.parent.core.AppBranding
import com.sdm3.parent.core.designsystem.component.Sdm3CategoryBadge
import com.sdm3.parent.core.designsystem.component.Sdm3Logo
import com.sdm3.parent.core.designsystem.theme.*
import com.sdm3.parent.core.navigation.PostAuthNavigator
import com.sdm3.parent.core.navigation.SDM3Route
import com.sdm3.parent.core.network.ApiError
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.core.security.SecureTokenManager
import com.sdm3.parent.domain.model.RoleContext
import com.sdm3.parent.domain.repository.AuthRepositoryContract
import kotlinx.coroutines.delay
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
    val glassSurface = ProductSchoolTheme.colors.liquidGlassSurface
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

    val logoAlphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = if (reducedMotion) snap() else tween(1000, easing = easeOutQuart),
        label = "logo_alpha"
    )

    // Delayed text animation
    var textStartAnimation by remember { mutableStateOf(isPreview) }
    val textAlphaAnim by animateFloatAsState(
        targetValue = if (textStartAnimation) 1f else 0f,
        animationSpec = if (reducedMotion) snap() else tween(1000, easing = easeOutQuart),
        label = "text_alpha"
    )

    val progressAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = if (reducedMotion) snap() else tween(2800, easing = easeOutCirc),
        label = "progress"
    )

    LaunchedEffect(Unit) {
        delay(300)
        startAnimation = true
        delay(400)
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
        
        // Multi-Orb Animated Mesh Background
        Canvas(modifier = Modifier.fillMaxSize().alpha(logoAlphaAnim)) {
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

        // Central Branding Stack
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-Spacing.xxl)) // Consistent vertical lift
                .padding(horizontal = Spacing.xl)
        ) {
            // Logo Container
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = scaleAnim
                        scaleY = scaleAnim
                        alpha = logoAlphaAnim
                    },
                contentAlignment = Alignment.Center
            ) {
                Sdm3Logo(
                    size = logoSize * 0.65f,
                    showBackground = false
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            // Typography Stack
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.graphicsLayer {
                    alpha = textAlphaAnim
                    translationY = (1f - textAlphaAnim) * 32f
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

                Sdm3CategoryBadge(
                    text = stringResource(Res.string.splash_subtitle).uppercase()
                )
            }
        }

        // Bottom Footer
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Modern Slim Progress Bar
            Box(
                modifier = Modifier
                    .width(160.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(
                        if (isDark) Color.White.copy(alpha = 0.08f)
                        else glassSurface.copy(alpha = 0.12f)
                    ),
                contentAlignment = Alignment.CenterStart
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progressAnim)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    colorScheme.secondary.copy(alpha = 0.5f),
                                    colorScheme.secondary,
                                    colorScheme.secondary.copy(alpha = 0.5f)
                                )
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(Spacing.lg))

            Text(
                text = AppBranding.SCHOOL_NAME.uppercase(),
                color = heroContent.copy(alpha = if (isDark) 0.5f else 0.35f),
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = if (isCompact) 1.sp else 2.5.sp,
                    fontWeight = FontWeight.Black
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
