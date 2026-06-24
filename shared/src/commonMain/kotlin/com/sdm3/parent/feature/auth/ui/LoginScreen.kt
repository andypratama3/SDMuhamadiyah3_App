package com.sdm3.parent.feature.auth.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.unit.dp
import com.sdm3.parent.core.designsystem.component.*
import com.sdm3.parent.core.designsystem.theme.*
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onForgotPassword: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val isPreview = LocalInspectionMode.current
    var startAnimation by remember { mutableStateOf(isPreview) }
    if (!isPreview) {
        LaunchedEffect(Unit) { startAnimation = true }
    }

    val reducedMotion = LocalReducedMotion.current
    val animatedAlpha: Float
    val animatedTranslationY: Float
    if (isPreview) {
        animatedAlpha = 1f
        animatedTranslationY = 0f
    } else {
        val spec: DurationBasedAnimationSpec<Float> = if (reducedMotion) snap() else tween(durationMillis = 700, easing = EaseOutQuart)
        val alpha by animateFloatAsState(
            targetValue = if (startAnimation) 1f else 0f,
            animationSpec = spec,
            label = "alpha"
        )
        animatedAlpha = alpha
        val transY by animateFloatAsState(
            targetValue = if (startAnimation) 0f else 40f,
            animationSpec = spec,
            label = "translationY"
        )
        animatedTranslationY = transY
    }

    val handleLogin: () -> Unit = {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        focusManager.clearFocus()
        if (email.isBlank() || password.isBlank()) {
            error = "Email dan password harus diisi"
        } else {
            isLoading = true
            error = null
            coroutineScope.launch {
                delay(1000)
                isLoading = false
                onLoginSuccess()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .padding(horizontal = Spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(Spacing.xxxl))

            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(44.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Sdm3Logo(size = 60.dp)
            }

            Spacer(modifier = Modifier.height(Spacing.lg))

            Text(
                text = "Selamat Datang",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(Spacing.xs))

            Text(
                text = "Masuk menggunakan akun orang tua.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(Spacing.xxl))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha = animatedAlpha
                        translationY = animatedTranslationY
                    },
                color = Color.Transparent
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Sdm3TextField(
                        value = email,
                        onValueChange = { email = it; error = null },
                        label = "Email",
                        leadingIcon = Icons.Outlined.Email,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        )
                    )

                    Spacer(modifier = Modifier.height(Spacing.md))

                    Sdm3TextField(
                        value = password,
                        onValueChange = { password = it; error = null },
                        label = "Password",
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

                    MotionAnim(
                        visible = error != null,
                        modifier = Modifier.align(Alignment.Start)
                    ) {
                        Text(
                            text = error ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = Spacing.xs, start = Spacing.xs)
                        )
                    }

                    Spacer(modifier = Modifier.height(Spacing.sm))

                    TextButton(
                        onClick = { onForgotPassword(email) },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            text = "Lupa password?",
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(Spacing.xl))

                    Sdm3Button(
                        text = "Masuk",
                        onClick = handleLogin,
                        isLoading = isLoading
                    )

                    Spacer(modifier = Modifier.height(Spacing.md))

                    Sdm3OutlinedButton(
                        text = "Biometrik",
                        onClick = { },
                        icon = Icons.Outlined.Fingerprint
                    )

                    Spacer(modifier = Modifier.height(Spacing.xxxl))
                }
            }
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    SDM3Theme {
        LoginScreen(onLoginSuccess = {}, onForgotPassword = {})
    }
}
