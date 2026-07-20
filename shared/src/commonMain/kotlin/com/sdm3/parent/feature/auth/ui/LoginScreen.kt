package com.sdm3.parent.feature.auth.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sdm3.parent.core.designsystem.component.*
import com.sdm3.parent.core.designsystem.theme.*
import com.sdm3.parent.feature.auth.LoginEffect
import com.sdm3.parent.feature.auth.LoginIntent
import com.sdm3.parent.feature.auth.LoginViewModel
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.resources.stringResource
import sdmuhammadiyah3samarinda.shared.generated.resources.Res
import sdmuhammadiyah3samarinda.shared.generated.resources.school_name

private val PremiumEasing = CubicBezierEasing(0.32f, 0.72f, 0f, 1f)

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
    onForgotPassword: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val haptic = LocalHapticFeedback.current
    val colorScheme = MaterialTheme.colorScheme

    val uiState by viewModel.uiState.collectAsState()

    var isPasswordVisible by remember { mutableStateOf(false) }

    val isPreview = LocalInspectionMode.current
    var startAnimation by remember { mutableStateOf(isPreview) }
    if (!isPreview) {
        LaunchedEffect(Unit) { startAnimation = true }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                LoginEffect.LoginSuccess -> {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onLoginSuccess()
                }
            }
        }
    }

    val handleLogin: () -> Unit = {
        focusManager.clearFocus()
        viewModel.onIntent(LoginIntent.Login)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        // Tap di area kosong saja — jangan bungkus Column form agar TextField
        // di iOS Simulator bisa menerima fokus + keyboard Mac (hardware keyboard).
        Box(
            modifier = Modifier
                .matchParentSize()
                .pointerInput(focusManager) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                }
        )

        Canvas(modifier = Modifier.fillMaxSize().alpha(0.5f)) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(colorScheme.primary.copy(alpha = 0.15f), Color.Transparent),
                    center = Offset(size.width * 0.85f, size.height * 0.15f),
                    radius = size.width * 1.2f
                )
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(colorScheme.secondary.copy(alpha = 0.12f), Color.Transparent),
                    center = Offset(size.width * 0.15f, size.height * 0.85f),
                    radius = size.width * 0.9f
                )
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(colorScheme.tertiary.copy(alpha = 0.08f), Color.Transparent),
                    center = Offset(size.width * 0.5f, size.height * 0.5f),
                    radius = size.width * 0.6f
                )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .safeDrawingPadding()
                .imePadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = if (startAnimation) 1f else 0.85f
                        scaleY = if (startAnimation) 1f else 0.85f
                        alpha = if (startAnimation) 1f else 0f
                    }
                    .animateContentSize(tween(700, easing = PremiumEasing)),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.size(110.dp).blur(if (startAnimation) 0.dp else 16.dp),
                    shape = RoundedCornerShape(28.dp),
                    color = glassSurfaceColor(),
                    border = BorderStroke(2.dp, glassBorderColor()),
                    shadowElevation = 8.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Sdm3Logo(size = 72.dp, showBackground = false)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.graphicsLayer {
                    alpha = if (startAnimation) 1f else 0f
                    translationY = if (startAnimation) 0f else 24f
                }
            ) {
                Text(
                    text = stringResource(Res.string.school_name),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary,
                    letterSpacing = (-0.3).sp,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = colorScheme.secondaryContainer.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(999.dp)
                ) {
                    Text(
                        text = " ORANG TUA & GURU ",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = colorScheme.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Sdm3GlassCard(
                modifier = Modifier.graphicsLayer {
                    alpha = if (startAnimation) 1f else 0f
                    translationY = if (startAnimation) 0f else 48f
                },
                padding = 24.dp
            ) {
                Column {
                    Sdm3TextField(
                        value = uiState.email,
                        onValueChange = { viewModel.onIntent(LoginIntent.EmailChanged(it)) },
                        label = "Email Institusi",
                        placeholder = "nama@sekolah.id",
                        leadingIcon = Icons.Outlined.Email,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Sdm3TextField(
                        value = uiState.password,
                        onValueChange = { viewModel.onIntent(LoginIntent.PasswordChanged(it)) },
                        label = "Kunci Akses",
                        placeholder = "••••••••",
                        leadingIcon = Icons.Outlined.Lock,
                        trailingIcon = if (isPasswordVisible) Icons.Outlined.VisibilityOff
                        else Icons.Outlined.Visibility,
                        onTrailingIconClick = { isPasswordVisible = !isPasswordVisible },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { handleLogin() }
                        )
                    )

                    AnimatedVisibility(
                        visible = uiState.errorMessage != null,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Surface(
                            modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
                            color = colorScheme.errorContainer,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, colorScheme.error.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Outlined.ErrorOutline,
                                    contentDescription = null,
                                    tint = colorScheme.error,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = uiState.errorMessage ?: "",
                                    color = colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(
                        onClick = { onForgotPassword(uiState.email) },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            text = "Lupa Kunci Akses?",
                            color = colorScheme.primary.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier.graphicsLayer {
                    alpha = if (startAnimation) 1f else 0f
                    translationY = if (startAnimation) 0f else 60f
                }
            ) {
                Sdm3Button(
                    text = "Masuk",
                    onClick = handleLogin,
                    isLoading = uiState.isLoading,
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                )

                if (uiState.biometricAvailable) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Sdm3OutlinedButton(
                        text = "Gunakan Biometrik",
                        onClick = { viewModel.onIntent(LoginIntent.BiometricLogin) },
                        icon = Icons.Outlined.Fingerprint,
                        contentColor = colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "${stringResource(Res.string.school_name)} v${com.sdm3.parent.APP_VERSION_NAME}",
                style = MaterialTheme.typography.labelSmall,
                color = colorScheme.primary.copy(alpha = 0.3f),
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    SDM3Theme {
        LoginScreen(
            viewModel = org.koin.compose.viewmodel.koinViewModel(),
            onLoginSuccess = {},
            onForgotPassword = {}
        )
    }
}
