package com.sdm3.parent.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
    @SerialName("device_name")
    val deviceName: String = "mobile"
)

@Serializable
data class LoginResponse(
    val user: UserDto,
    val token: String
)

@Serializable
data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val phone: String? = null,
    val avatar: String? = null
)

@Serializable
data class ForgotPasswordRequest(
    val email: String
)

@Serializable
data class ForgotPasswordResponse(
    val message: String
)

@Serializable
data class VerifyOtpRequest(
    val email: String,
    val otp: String
)

@Serializable
data class VerifyOtpResponse(
    val message: String,
    val token: String? = null
)

@Serializable
data class ResetPasswordRequest(
    val email: String,
    val otp: String,
    val password: String,
    @SerialName("password_confirmation")
    val passwordConfirmation: String
)

@Serializable
data class ResetPasswordResponse(
    val message: String
)
