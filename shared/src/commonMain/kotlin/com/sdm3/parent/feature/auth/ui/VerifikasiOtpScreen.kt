package com.sdm3.parent.feature.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sdm3.parent.core.designsystem.component.Sdm3Button
import com.sdm3.parent.core.designsystem.component.Sdm3TextField
import com.sdm3.parent.core.designsystem.theme.OnSurfaceVariant
import com.sdm3.parent.core.designsystem.theme.Primary
import com.sdm3.parent.core.designsystem.theme.SchoolGreenDark
import com.sdm3.parent.core.designsystem.theme.Spacing
import com.sdm3.parent.core.designsystem.theme.StatusSuccess
import com.sdm3.parent.feature.auth.OtpStep
import com.sdm3.parent.feature.auth.VerifikasiOtpViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import com.sdm3.parent.core.designsystem.theme.Secondary
import com.sdm3.parent.core.designsystem.theme.StatusDanger

@Composable
fun VerifikasiOtpScreen(
    viewModel: VerifikasiOtpViewModel,
    onSuccess: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(Spacing.xl))

        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            modifier = Modifier.size(40.dp),
            tint = Primary
        )

        Spacer(modifier = Modifier.height(Spacing.md))

        Text(
            text = "Lupa Password",
            color = SchoolGreenDark,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp
        )

        Spacer(modifier = Modifier.height(Spacing.sm))

        Text(
            text = when (state.step) {
                OtpStep.REQUEST_OTP -> "Masukkan email untuk menerima kode OTP"
                OtpStep.VERIFY_OTP -> "Masukkan 6 digit kode OTP yang dikirim ke email"
                OtpStep.RESET_PASSWORD -> "Buat password baru untuk akun Anda"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.lg))

        when (state.step) {
            OtpStep.REQUEST_OTP -> {
                Sdm3TextField(
                    value = state.email,
                    onValueChange = { viewModel.setEmail(it) },
                    label = "Email",
                    leadingIcon = Icons.Default.Email
                )

                Spacer(modifier = Modifier.height(Spacing.lg))

                Sdm3Button(
                    text = "Kirim OTP",
                    onClick = { viewModel.requestOtp() },
                    isLoading = state.isLoading
                )
            }

            OtpStep.VERIFY_OTP -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Email, contentDescription = null, tint = Secondary)
                    Spacer(Modifier.width(8.dp))
                    Text(state.email, style = MaterialTheme.typography.bodyLarge)
                }

                Spacer(modifier = Modifier.height(Spacing.lg))

                OtpDigitInput(
                    code = state.otpCode,
                    onCodeChanged = { viewModel.updateOtpCode(it) }
                )

                Spacer(modifier = Modifier.height(Spacing.md))

                if (state.countdownSeconds > 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(16.dp), tint = OnSurfaceVariant)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "Kirim ulang dalam ${state.countdownSeconds} detik",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant
                        )
                    }
                } else {
                    TextButton(onClick = { viewModel.resendOtp() }) {
                        Text(
                            text = "Kirim Ulang OTP",
                            color = Primary,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.lg))

                Sdm3Button(
                    text = "Verifikasi",
                    onClick = { viewModel.verifyOtp() },
                    isLoading = state.isLoading,
                    enabled = state.otpCode.length == 6
                )
            }

            OtpStep.RESET_PASSWORD -> {
                Sdm3TextField(
                    value = state.newPassword,
                    onValueChange = { viewModel.updateNewPassword(it) },
                    label = "Password Baru",
                    leadingIcon = Icons.Default.Lock,
                    visualTransformation = if (state.isPasswordVisible)
                        VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                Spacer(modifier = Modifier.height(Spacing.md))

                Sdm3TextField(
                    value = state.newPasswordConfirmation,
                    onValueChange = { viewModel.updateNewPasswordConfirmation(it) },
                    label = "Konfirmasi Password",
                    leadingIcon = Icons.Default.Lock,
                    visualTransformation = if (state.isPasswordVisible)
                        VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                Spacer(modifier = Modifier.height(Spacing.sm))

                TextButton(onClick = { viewModel.togglePasswordVisibility() }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (state.isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = if (state.isPasswordVisible) "Sembunyikan" else "Tampilkan",
                            color = OnSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.lg))

                Sdm3Button(
                    text = "Reset Password",
                    onClick = { viewModel.resetPassword() },
                    isLoading = state.isLoading
                )
            }
        }

        state.errorMessage?.let {
            Spacer(modifier = Modifier.height(Spacing.sm))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp), tint = StatusDanger)
                Spacer(Modifier.width(4.dp))
                Text(it, color = StatusDanger, style = MaterialTheme.typography.bodyMedium)
            }
        }

        state.resetSuccessMessage?.let {
            Spacer(modifier = Modifier.height(Spacing.md))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp), tint = StatusSuccess)
                Spacer(Modifier.width(4.dp))
                Text(it, color = StatusSuccess, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.height(Spacing.lg))
            Sdm3Button(
                text = "Kembali ke Login",
                onClick = onSuccess
            )
        }
    }
}

@Composable
private fun OtpDigitInput(
    code: String,
    onCodeChanged: (String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0 until 6) {
                val digit = if (i < code.length) code[i].toString() else ""
                val isFocused = i == code.length
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            width = if (isFocused) 2.dp else 1.dp,
                            color = if (isFocused) MaterialTheme.colorScheme.secondary
                            else MaterialTheme.colorScheme.outlineVariant,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .background(
                            if (digit.isNotEmpty()) MaterialTheme.colorScheme.surfaceContainerLow
                            else MaterialTheme.colorScheme.surface
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = digit,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(Spacing.sm))

        Text(
            text = "Ketik kode OTP 6 digit di sini",
            style = MaterialTheme.typography.bodySmall,
            color = OnSurfaceVariant,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }

    // Hidden TextField for OTP input
    Sdm3TextField(
        value = code,
        onValueChange = { onCodeChanged(it.filter { c -> c.isDigit() }.take(6)) },
        label = "Kode OTP",
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}
