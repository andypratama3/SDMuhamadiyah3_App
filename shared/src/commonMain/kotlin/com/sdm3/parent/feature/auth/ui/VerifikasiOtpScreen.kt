package com.sdm3.parent.feature.auth.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
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
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val isPreview = LocalInspectionMode.current
    val state by if (isPreview) {
        remember { mutableStateOf(VerifikasiOtpUiState()) }
    } else {
        viewModel.uiState.collectAsState()
    }
    val colorScheme = MaterialTheme.colorScheme

    ScreenScaffold(
        title = when(state.step) {
            OtpStep.REQUEST_OTP -> "Akses Pemulihan"
            OtpStep.VERIFY_OTP -> "Verifikasi Akun"
            OtpStep.RESET_PASSWORD -> "Pembaruan Kunci"
        },
        subtitle = "KEAMANAN DIGITAL",
        onBack = onBack
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ScreenGlowBackground()

            Spacer(modifier = Modifier.height(Spacing.xxl))

            Sdm3GlassIconContainer(size = 100.dp) {
                Icon(
                    imageVector = when(state.step) {
                        OtpStep.REQUEST_OTP -> Icons.Outlined.Email
                        OtpStep.VERIFY_OTP -> Icons.Outlined.Lock
                        OtpStep.RESET_PASSWORD -> Icons.Outlined.CheckCircle
                    },
                    contentDescription = when(state.step) {
                        OtpStep.REQUEST_OTP -> "Email"
                        OtpStep.VERIFY_OTP -> "Verifikasi"
                        OtpStep.RESET_PASSWORD -> "Selesai"
                    },
                    modifier = Modifier.size(40.dp),
                    tint = colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xxl))

            Text(
                text = when(state.step) {
                    OtpStep.REQUEST_OTP -> "Akses Pemulihan"
                    OtpStep.VERIFY_OTP -> "Verifikasi Akun"
                    OtpStep.RESET_PASSWORD -> "Pembaruan Kunci"
                },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            Text(
                text = when (state.step) {
                    OtpStep.REQUEST_OTP -> "Masukkan email institusi Anda untuk menerima kode pemulihan."
                    OtpStep.VERIFY_OTP -> "Kami telah mengirimkan 6-digit kode keamanan ke email ${state.email}."
                    OtpStep.RESET_PASSWORD -> "Silakan buat kunci akses baru yang kuat untuk keamanan akun Anda."
                },
                style = MaterialTheme.typography.bodyLarge,
                color = ProductSchoolTheme.colors.onSurfaceMuted,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                modifier = Modifier.padding(horizontal = Spacing.md)
            )

            Spacer(modifier = Modifier.height(Spacing.xxxl))

            Sdm3Card(
                modifier = Modifier.fillMaxWidth(),
                padding = 28.dp
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

                            Spacer(modifier = Modifier.height(Spacing.xl))

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

                            Spacer(modifier = Modifier.height(Spacing.xxl))

                            if (state.countdownSeconds > 0) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Outlined.Timer, contentDescription = "Timer", modifier = Modifier.size(16.dp), tint = ProductSchoolTheme.colors.onSurfaceMuted)
                                    Spacer(Modifier.width(Spacing.xs))
                                    Text(
                                        text = "Kirim ulang dalam ${state.countdownSeconds}d",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = ProductSchoolTheme.colors.onSurfaceMuted,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "KIRIM ULANG KODE",
                                        color = colorScheme.primary,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { viewModel.resendOtp() }
                                            .padding(Spacing.sm),
                                        letterSpacing = 1.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(Spacing.xl))

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

                            Spacer(modifier = Modifier.height(Spacing.lg))

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

                            Spacer(modifier = Modifier.height(Spacing.xl))

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
                Sdm3ErrorBanner(message = state.errorMessage ?: "")
            }

            AnimatedVisibility(visible = state.resetSuccessMessage != null) {
                val successColor = statusSuccessColor()
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 24.dp)) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = successColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, successColor.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.CheckCircle, contentDescription = "Berhasil", tint = successColor, modifier = Modifier.size(22.dp))
                            Spacer(Modifier.width(Spacing.sm))
                            Text(state.resetSuccessMessage ?: "", color = successColor, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Spacer(modifier = Modifier.height(Spacing.xl))
                    Sdm3Button(
                        text = "Kembali ke Login",
                        onClick = onSuccess,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xxxl))
        }
    }
}

@Composable
private fun OtpDigitInput(
    code: String,
    onCodeChanged: (String) -> Unit,
    colorScheme: androidx.compose.material3.ColorScheme
) {
    val focusRequester = remember { FocusRequester() }
    val isPreview = LocalInspectionMode.current
    val glassSurface = ProductSchoolTheme.colors.liquidGlassSurface
    val glassBorder = ProductSchoolTheme.colors.liquidGlassBorder

    LaunchedEffect(Unit) {
        if (!isPreview) {
            runCatching { focusRequester.requestFocus() }
        }
    }

    BasicTextField(
        value = code,
        onValueChange = { raw ->
            val digitsOnly = raw.filter { c -> c.isDigit() }.take(6)
            onCodeChanged(digitsOnly)
        },
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        cursorBrush = SolidColor(Color.Transparent),
        decorationBox = {
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
                            .height(64.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isFocused) colorScheme.primaryContainer.copy(alpha = 0.3f)
                                else Color.Transparent
                            )
                            .border(
                                width = if (isFocused) 2.dp else 1.5.dp,
                                color = if (isFocused) colorScheme.primary else glassBorder,
                                shape = RoundedCornerShape(16.dp)
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
    )
}

@Preview
@Composable
private fun VerifikasiOtpScreenPreview() {
    val mockAuthRepo = remember {
        object : com.sdm3.parent.domain.repository.AuthRepositoryContract {
            override suspend fun login(email: String, password: String) = com.sdm3.parent.core.network.ApiResult.Error(com.sdm3.parent.core.network.ApiError.Unknown("preview"))
            override suspend fun getAuthenticatedUser() = com.sdm3.parent.core.network.ApiResult.Error(com.sdm3.parent.core.network.ApiError.Unknown("preview"))
            override fun resolveStoredRoleContext() = com.sdm3.parent.domain.model.RoleContext(
                com.sdm3.parent.domain.model.UserRole.PARENT,
                hasParentAccess = true,
                hasTeacherAccess = false,
            )
            override suspend fun apiLogout() = com.sdm3.parent.core.network.ApiResult.Error(com.sdm3.parent.core.network.ApiError.Unknown("preview"))
            override suspend fun deleteAccount(reason: String) = com.sdm3.parent.core.network.ApiResult.Error(com.sdm3.parent.core.network.ApiError.Unknown("preview"))
            override suspend fun isLoggedIn() = false
            override suspend fun clearLocalSession() {}
            override suspend fun logout() {}
            override suspend fun requestOtp(email: String) = com.sdm3.parent.core.network.ApiResult.Error(com.sdm3.parent.core.network.ApiError.Unknown("preview"))
            override suspend fun verifyOtp(email: String, otp: String) = com.sdm3.parent.core.network.ApiResult.Error(com.sdm3.parent.core.network.ApiError.Unknown("preview"))
            override suspend fun resetPassword(email: String, otp: String, password: String, passwordConfirmation: String) = com.sdm3.parent.core.network.ApiResult.Error(com.sdm3.parent.core.network.ApiError.Unknown("preview"))
        }
    }
    SDM3Theme {
        VerifikasiOtpScreen(
            viewModel = VerifikasiOtpViewModel(mockAuthRepo),
            onBack = {},
            onSuccess = {}
        )
    }
}
