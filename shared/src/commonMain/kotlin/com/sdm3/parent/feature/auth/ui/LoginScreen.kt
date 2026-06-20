package com.sdm3.parent.feature.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import com.sdm3.parent.core.designsystem.component.Sdm3Button
import com.sdm3.parent.core.designsystem.component.Sdm3TextField
import com.sdm3.parent.core.designsystem.theme.OnSurfaceVariant
import com.sdm3.parent.core.designsystem.theme.SchoolGreenDark
import com.sdm3.parent.core.designsystem.theme.Spacing

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onForgotPassword: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(Spacing.xl))

        Text(
            text = "SDM3",
            color = SchoolGreenDark,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp
        )

        Spacer(modifier = Modifier.height(Spacing.lg))

        Text(
            text = "Masuk ke Akun Anda",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(Spacing.sm))

        Text(
            text = "Silakan masukkan email dan password wali murid untuk melanjutkan",
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.lg))

        Sdm3TextField(
            value = email,
            onValueChange = { email = it; error = null },
            label = "Email",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            )
        )

        Spacer(modifier = Modifier.height(Spacing.md))

        Sdm3TextField(
            value = password,
            onValueChange = { password = it; error = null },
            label = "Password",
            visualTransformation = if (isPasswordVisible)
                VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            )
        )

        error?.let {
            Spacer(modifier = Modifier.height(Spacing.sm))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(Spacing.lg))

        Sdm3Button(
            text = "Masuk",
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    error = "Email dan password harus diisi"
                    return@Sdm3Button
                }
                isLoading = true
                onLoginSuccess()
            },
            isLoading = isLoading
        )

        Spacer(modifier = Modifier.height(Spacing.md))

        TextButton(
            text = "Lupa password? Reset via OTP",
            onClick = { onForgotPassword(email) }
        )
    }
}

@Composable
private fun TextButton(
    text: String,
    onClick: () -> Unit
) {
    androidx.compose.material3.TextButton(onClick = onClick) {
        Text(
            text = text,
            color = OnSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
