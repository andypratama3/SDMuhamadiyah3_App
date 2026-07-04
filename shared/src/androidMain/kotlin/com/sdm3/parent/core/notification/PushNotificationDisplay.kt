package com.sdm3.parent.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.RemoteMessage
import com.sdm3.parent.shared.R

object PushNotificationDisplay {
    private const val CHANNEL_ID = "sdm3_parent_default"
    private const val CHANNEL_NAME = "Notifikasi SDM3"

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        if (manager.getNotificationChannel(CHANNEL_ID) != null) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Pengumuman, pembayaran, nilai, dan kehadiran siswa"
        }
        manager.createNotificationChannel(channel)
    }

    fun show(context: Context, message: RemoteMessage) {
        ensureChannel(context)
        val title = message.notification?.title
            ?: message.data["title"]
            ?: "SD Muhammadiyah 3 Samarinda"
        val body = message.notification?.body
            ?: message.data["message"]
            ?: message.data["body"]
            ?: return

        val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
            ?.apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                message.data.forEach { (key, value) -> putExtra(key, value) }
                message.data["notification_id"]?.let { putExtra("notification_id", it) }
                message.data["type"]?.let { putExtra("notification_type", it) }
            } ?: return

        val pendingIntent = PendingIntent.getActivity(
            context,
            message.hashCode(),
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        manager.notify(message.hashCode(), notification)
    }
}
