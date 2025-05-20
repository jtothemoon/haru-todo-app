package com.haru.todo.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            "reset_channel",
            "할 일 초기화 알림",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "하루 할 일 자동 초기화 시 안내 알림"
        }
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
