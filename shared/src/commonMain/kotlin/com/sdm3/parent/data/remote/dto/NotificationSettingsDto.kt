package com.sdm3.parent.data.remote.dto

import com.sdm3.parent.feature.profil.NotificationSettings
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationSettingsDto(
    @SerialName("push_enabled")
    val pushEnabled: Boolean = true,
    @SerialName("email_enabled")
    val emailEnabled: Boolean = false,
    @SerialName("sms_enabled")
    val smsEnabled: Boolean = false,
    @SerialName("nilai_notif")
    val nilaiNotif: Boolean = true,
    @SerialName("tagihan_notif")
    val tagihanNotif: Boolean = true,
    @SerialName("pengumuman_notif")
    val pengumumanNotif: Boolean = true,
    @SerialName("kehadiran_notif")
    val kehadiranNotif: Boolean = true,
    @SerialName("rapor_notif")
    val raporNotif: Boolean = false,
)

fun NotificationSettingsDto.toDomain(): NotificationSettings = NotificationSettings(
    pushEnabled = pushEnabled,
    emailEnabled = emailEnabled,
    smsEnabled = smsEnabled,
    nilaiNotif = nilaiNotif,
    tagihanNotif = tagihanNotif,
    pengumumanNotif = pengumumanNotif,
    kehadiranNotif = kehadiranNotif,
    raporNotif = raporNotif,
)

fun NotificationSettings.toDto(): NotificationSettingsDto = NotificationSettingsDto(
    pushEnabled = pushEnabled,
    emailEnabled = emailEnabled,
    smsEnabled = smsEnabled,
    nilaiNotif = nilaiNotif,
    tagihanNotif = tagihanNotif,
    pengumumanNotif = pengumumanNotif,
    kehadiranNotif = kehadiranNotif,
    raporNotif = raporNotif,
)
