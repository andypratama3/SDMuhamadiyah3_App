package com.sdm3.parent.feature.auth.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val PremiumEasing = CubicBezierEasing(0.32f, 0.72f, 0f, 1f)

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onForgotPassword: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val colorScheme = MaterialTheme.colorScheme

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

    val handleLogin: () -> Unit = {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        focusManager.clearFocus()
        if (email.isBlank() || password.isBlank()) {
            error = "Identitas dan kunci akses diperlukan"
        } else {
            isLoading = true
            error = null
            coroutineScope.launch {
                delay(1500)
                isLoading = false
                onLoginSuccess()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        // EduOcto Atmospheric Glows
        Canvas(modifier = Modifier.fillMaxSize().alpha(0.4f)) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(colorScheme.primaryContainer, Color.Transparent),
                    center = Offset(size.width * 0.8f, size.height * 0.1f),
                    radius = size.width
                )
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(colorScheme.secondary.copy(alpha = 0.1f), Color.Transparent),
                    center = Offset(size.width * 0.2f, size.height * 0.9f),
                    radius = size.width
                )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Institutional Logo Stack
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = if (startAnimation) 1f else 0.8f
                        scaleY = if (startAnimation) 1f else 0.8f
                        alpha = if (startAnimation) 1f else 0f
                    }
                    .animateContentSize(tween(600, easing = PremiumEasing)),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.size(100.dp).blur(if (startAnimation) 0.dp else 20.dp),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.5f),
                    border = BorderStroke(1.5.dp, Color.White.copy(alpha = 0.8f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Sdm3Logo(size = 70.dp, showBackground = false)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Editorial Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.graphicsLayer {
                    alpha = if (startAnimation) 1f else 0f
                    translationY = if (startAnimation) 0f else 20f
                }
            ) {
                Text(
                    text = "Portal Orang Tua",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary,
                    letterSpacing = (-0.5).sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = colorScheme.secondary.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(999.dp)
                ) {
                    Text(
                        text = " SD MUHAMMADIYAH 3 SAMARINDA ",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = colorScheme.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Glassmorphic Input Container
            Sdm3Card(
                modifier = Modifier.graphicsLayer {
                    alpha = if (startAnimation) 1f else 0f
                    translationY = if (startAnimation) 0f else 40f
                },
                padding = 20.dp
            ) {
                Column {
                    Sdm3TextField(
                        value = email,
                        onValueChange = { email = it; error = null },
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
                        value = password,
                        onValueChange = { password = it; error = null },
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
                        visible = error != null,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Surface(
                            modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
                            color = colorScheme.error.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = error ?: "",
                                color = colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(12.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(
                        onClick = { onForgotPassword(email) },
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

            // Primary Actions
            Column(
                modifier = Modifier.graphicsLayer {
                    alpha = if (startAnimation) 1f else 0f
                    translationY = if (startAnimation) 0f else 60f
                }
            ) {
                Sdm3Button(
                    text = "Masuk Ke Portal",
                    onClick = handleLogin,
                    isLoading = isLoading,
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Sdm3OutlinedButton(
                    text = "Gunakan Biometrik",
                    onClick = { },
                    icon = Icons.Outlined.Fingerprint,
                    contentColor = colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "EduOcto v2.4.0 • Academic Intelligence",
                style = MaterialTheme.typography.labelSmall,
                color = colorScheme.primary.copy(alpha = 0.3f),
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            
            Spacer(modifier = Modifier.height(20.dp))
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
