package com.sdm3.parent.feature.auth.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sdm3.parent.core.designsystem.component.Sdm3Button
import com.sdm3.parent.core.designsystem.component.Sdm3TextField
import com.sdm3.parent.core.designsystem.theme.*
import com.sdm3.parent.feature.auth.OtpStep
import com.sdm3.parent.feature.auth.VerifikasiOtpViewModel

@Composable
fun VerifikasiOtpScreen(
    viewModel: VerifikasiOtpViewModel,
    onSuccess: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(Spacing.xxxl))

        Surface(
            modifier = Modifier.size(88.dp),
            shape = RoundedCornerShape(28.dp),
            color = colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = when(state.step) {
                        OtpStep.REQUEST_OTP -> Icons.Outlined.Email
                        OtpStep.VERIFY_OTP -> Icons.Outlined.Lock
                        OtpStep.RESET_PASSWORD -> Icons.Outlined.CheckCircle
                    },
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.xl))

        Text(
            text = when(state.step) {
                OtpStep.REQUEST_OTP -> "Pemulihan Akun"
                OtpStep.VERIFY_OTP -> "Verifikasi Kode"
                OtpStep.RESET_PASSWORD -> "Kata Sandi Baru"
            },
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(Spacing.sm))

        Text(
            text = when (state.step) {
                OtpStep.REQUEST_OTP -> "Masukkan email untuk menerima instruksi pemulihan."
                OtpStep.VERIFY_OTP -> "Kami telah mengirimkan 6 digit kode keamanan ke email ${state.email}."
                OtpStep.RESET_PASSWORD -> "Silakan buat kata sandi baru yang kuat untuk akun Anda."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = Spacing.md)
        )

        Spacer(modifier = Modifier.height(Spacing.xl))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = CardShape,
            color = colorScheme.surface,
            tonalElevation = 0.dp
        ) {
            Column(modifier = Modifier.padding(Spacing.xl)) {
                when (state.step) {
                    OtpStep.REQUEST_OTP -> {
                        Sdm3TextField(
                            value = state.email,
                            onValueChange = { viewModel.setEmail(it) },
                            label = "Alamat Email",
                            leadingIcon = Icons.Outlined.Email,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                        )

                        Spacer(modifier = Modifier.height(Spacing.xl))

                        Sdm3Button(
                            text = "Kirim Kode Verifikasi",
                            onClick = { viewModel.requestOtp() },
                            isLoading = state.isLoading
                        )
                    }

                    OtpStep.VERIFY_OTP -> {
                        OtpDigitInput(
                            code = state.otpCode,
                            onCodeChanged = { viewModel.updateOtpCode(it) },
                            colorScheme = colorScheme
                        )

                        Spacer(modifier = Modifier.height(Spacing.xl))

                        if (state.countdownSeconds > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Outlined.Timer, contentDescription = null, modifier = Modifier.size(18.dp), tint = colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                                Spacer(Modifier.width(Spacing.sm))
                                Text(
                                    text = "Kirim ulang dalam ${state.countdownSeconds}s",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        } else {
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = "Kirim Ulang Kode OTP",
                                    color = colorScheme.primary,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { viewModel.resendOtp() }
                                        .padding(8.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(Spacing.lg))

                        Sdm3Button(
                            text = "Verifikasi Sekarang",
                            onClick = { viewModel.verifyOtp() },
                            isLoading = state.isLoading,
                            enabled = state.otpCode.length == 6
                        )
                    }

                    OtpStep.RESET_PASSWORD -> {
                        Sdm3TextField(
                            value = state.newPassword,
                            onValueChange = { viewModel.updateNewPassword(it) },
                            label = "Kata Sandi Baru",
                            leadingIcon = Icons.Outlined.Lock,
                            trailingIcon = if (state.isPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            onTrailingIconClick = { viewModel.togglePasswordVisibility() },
                            visualTransformation = if (state.isPasswordVisible)
                                VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                        )

                        Spacer(modifier = Modifier.height(Spacing.md))

                        Sdm3TextField(
                            value = state.newPasswordConfirmation,
                            onValueChange = { viewModel.updateNewPasswordConfirmation(it) },
                            label = "Konfirmasi Kata Sandi",
                            leadingIcon = Icons.Outlined.Lock,
                            trailingIcon = if (state.isPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            onTrailingIconClick = { viewModel.togglePasswordVisibility() },
                            visualTransformation = if (state.isPasswordVisible)
                                VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                        )

                        Spacer(modifier = Modifier.height(Spacing.xl))

                        Sdm3Button(
                            text = "Atur Ulang Sandi",
                            onClick = { viewModel.resetPassword() },
                            isLoading = state.isLoading
                        )
                    }
                }
            }
        }

        AnimatedVisibility(visible = state.errorMessage != null) {
            Surface(
                modifier = Modifier
                    .padding(top = Spacing.lg)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = StatusDangerBg
            ) {
                Row(
                    modifier = Modifier.padding(Spacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.ErrorOutline, contentDescription = null, tint = StatusDanger)
                    Spacer(Modifier.width(Spacing.sm))
                    Text(state.errorMessage ?: "", color = StatusDanger, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        AnimatedVisibility(visible = state.resetSuccessMessage != null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    modifier = Modifier
                        .padding(top = Spacing.lg)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = StatusSuccessBg
                ) {
                    Row(
                        modifier = Modifier.padding(Spacing.md),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = StatusSuccess)
                        Spacer(Modifier.width(Spacing.sm))
                        Text(state.resetSuccessMessage ?: "", color = StatusSuccess, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    }
                }
                Spacer(modifier = Modifier.height(Spacing.xl))
                Sdm3Button(
                    text = "Kembali ke Login",
                    onClick = onSuccess
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.xxxl))
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
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0 until 6) {
                val digit = if (i < code.length) code[i].toString() else ""
                val isFocused = i == code.length

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .clip(OtpBoxShape)
                        .background(
                            if (isFocused) colorScheme.primaryContainer
                            else colorScheme.surfaceVariant
                        )
                        .border(
                            width = if (isFocused) 2.dp else 0.dp,
                            color = if (isFocused) colorScheme.primary else Color.Transparent,
                            shape = OtpBoxShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (digit.isNotEmpty()) "*" else "",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isFocused) colorScheme.primary else colorScheme.onSurface
                    )
                    if (digit.isNotEmpty()) {
                        Text(
                            text = digit,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isFocused) colorScheme.primary else colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(Spacing.lg))

        Surface(
            shape = ChipShape,
            color = colorScheme.surfaceVariant
        ) {
            Text(
                text = "Masukkan kode rahasia 6-digit",
                style = MaterialTheme.typography.labelMedium,
                color = colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }

    Box(modifier = Modifier.size(1.dp).alpha(0f)) {
        Sdm3TextField(
            value = code,
            onValueChange = { onCodeChanged(it.filter { c -> c.isDigit() }.take(6)) },
            label = "",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
    }
}
