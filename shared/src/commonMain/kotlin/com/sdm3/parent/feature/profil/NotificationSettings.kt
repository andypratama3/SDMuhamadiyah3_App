package com.sdm3.parent.feature.profil

import kotlinx.serialization.Serializable

@Serializable
data class NotificationSettings(
    val pushEnabled: Boolean = true,
    val emailEnabled: Boolean = false,
    val smsEnabled: Boolean = false,
    val nilaiNotif: Boolean = true,
    val tagihanNotif: Boolean = true,
    val pengumumanNotif: Boolean = true,
    val kehadiranNotif: Boolean = true,
    val raporNotif: Boolean = false
)
