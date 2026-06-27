package com.sdm3.parent.feature.auth.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sdm3.parent.core.designsystem.component.*
import com.sdm3.parent.core.designsystem.theme.*
import com.sdm3.parent.feature.auth.OtpStep
import com.sdm3.parent.feature.auth.VerifikasiOtpUiState
import com.sdm3.parent.feature.auth.VerifikasiOtpViewModel

@Composable
fun VerifikasiOtpScreen(
    viewModel: VerifikasiOtpViewModel,
    onSuccess: () -> Unit
) {
    val isPreview = LocalInspectionMode.current
    val state by if (isPreview) {
        remember { mutableStateOf(VerifikasiOtpUiState()) }
    } else {
        viewModel.uiState.collectAsState()
    }
    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        // Atmospheric Glow
        Canvas(modifier = Modifier.fillMaxSize().alpha(0.3f)) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(colorScheme.primaryContainer, Color.Transparent),
                    center = Offset(size.width, 0f),
                    radius = size.width * 1.5f
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

            // Step Indicator Icon
            Surface(
                modifier = Modifier.size(90.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color.White.copy(alpha = 0.5f),
                border = BorderStroke(1.5.dp, Color.White.copy(alpha = 0.8f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = when(state.step) {
                            OtpStep.REQUEST_OTP -> Icons.Outlined.Email
                            OtpStep.VERIFY_OTP -> Icons.Outlined.Lock
                            OtpStep.RESET_PASSWORD -> Icons.Outlined.CheckCircle
                        },
                        contentDescription = null,
                        modifier = Modifier.size(36.dp),
                        tint = colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = when(state.step) {
                    OtpStep.REQUEST_OTP -> "Akses Pemulihan"
                    OtpStep.VERIFY_OTP -> "Verifikasi Akun"
                    OtpStep.RESET_PASSWORD -> "Pembaruan Kunci"
                },
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary,
                letterSpacing = (-1).sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = when (state.step) {
                    OtpStep.REQUEST_OTP -> "Masukkan email institusi Anda untuk menerima kode pemulihan."
                    OtpStep.VERIFY_OTP -> "Kami telah mengirimkan 6-digit kode keamanan ke email ${state.email}."
                    OtpStep.RESET_PASSWORD -> "Silakan buat kunci akses baru yang kuat untuk keamanan akun Anda."
                },
                style = MaterialTheme.typography.bodyLarge,
                color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            Sdm3Card(
                modifier = Modifier.fillMaxWidth(),
                padding = 24.dp
            ) {
                Column {
                    when (state.step) {
                        OtpStep.REQUEST_OTP -> {
                            Sdm3TextField(
                                value = state.email,
                                onValueChange = { viewModel.setEmail(it) },
                                label = "Email Institusi",
                                leadingIcon = Icons.Outlined.Email,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Sdm3Button(
                                text = "Kirim Instruksi",
                                onClick = { viewModel.requestOtp() },
                                isLoading = state.isLoading,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        OtpStep.VERIFY_OTP -> {
                            OtpDigitInput(
                                code = state.otpCode,
                                onCodeChanged = { viewModel.updateOtpCode(it) },
                                colorScheme = colorScheme
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            if (state.countdownSeconds > 0) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Outlined.Timer, contentDescription = null, modifier = Modifier.size(16.dp), tint = colorScheme.primary.copy(alpha = 0.4f))
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = "Kirim ulang dalam ${state.countdownSeconds}d",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = colorScheme.primary.copy(alpha = 0.4f),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "KIRIM ULANG KODE",
                                        color = colorScheme.secondary,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { viewModel.resendOtp() }
                                            .padding(12.dp),
                                        letterSpacing = 1.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Sdm3Button(
                                text = "Verifikasi Kunci",
                                onClick = { viewModel.verifyOtp() },
                                isLoading = state.isLoading,
                                enabled = state.otpCode.length == 6,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        OtpStep.RESET_PASSWORD -> {
                            Sdm3TextField(
                                value = state.newPassword,
                                onValueChange = { viewModel.updateNewPassword(it) },
                                label = "Kunci Akses Baru",
                                leadingIcon = Icons.Outlined.Lock,
                                trailingIcon = if (state.isPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                onTrailingIconClick = { viewModel.togglePasswordVisibility() },
                                visualTransformation = if (state.isPasswordVisible)
                                    VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Sdm3TextField(
                                value = state.newPasswordConfirmation,
                                onValueChange = { viewModel.updateNewPasswordConfirmation(it) },
                                label = "Konfirmasi Kunci",
                                leadingIcon = Icons.Outlined.Lock,
                                trailingIcon = if (state.isPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                onTrailingIconClick = { viewModel.togglePasswordVisibility() },
                                visualTransformation = if (state.isPasswordVisible)
                                    VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Sdm3Button(
                                text = "Simpan Perubahan",
                                onClick = { viewModel.resetPassword() },
                                isLoading = state.isLoading,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(visible = state.errorMessage != null) {
                Surface(
                    modifier = Modifier.padding(top = 24.dp).fillMaxWidth(),
                    color = colorScheme.error.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.ErrorOutline, contentDescription = null, tint = colorScheme.error, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(12.dp))
                        Text(state.errorMessage ?: "", color = colorScheme.error, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }

            AnimatedVisibility(visible = state.resetSuccessMessage != null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 24.dp)) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = StatusSuccess.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = StatusSuccess, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(12.dp))
                            Text(state.resetSuccessMessage ?: "", color = StatusSuccess, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Sdm3Button(
                        text = "Kembali ke Login",
                        onClick = onSuccess,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun OtpDigitInput(
    code: String,
    onCodeChanged: (String) -> Unit,
    colorScheme: androidx.compose.material3.ColorScheme
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0 until 6) {
                val digit = if (i < code.length) code[i].toString() else ""
                val isFocused = i == code.length

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (isFocused) colorScheme.primary.copy(alpha = 0.05f)
                            else Color.White.copy(alpha = 0.5f)
                        )
                        .border(
                            width = if (isFocused) 2.dp else 1.dp,
                            color = if (isFocused) colorScheme.primary else Color.White.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (digit.isNotEmpty()) {
                        Text(
                            text = digit,
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary
                        )
                    } else if (isFocused) {
                        Box(modifier = Modifier.size(2.dp, 24.dp).background(colorScheme.primary.copy(alpha = 0.4f)))
                    }
                }
            }
        }
    }

    // Hidden input field for OTP
    Box(modifier = Modifier.size(1.dp).alpha(0f)) {
        Sdm3TextField(
            value = code,
            onValueChange = { 
                if (it.length <= 6) onCodeChanged(it.filter { c -> c.isDigit() }) 
            },
            label = "",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
    }
}

@Preview
@Composable
private fun VerifikasiOtpScreenPreview() {
    SDM3Theme {
        VerifikasiOtpScreen(
            viewModel = VerifikasiOtpViewModel(),
            onSuccess = {}
        )
    }
}
