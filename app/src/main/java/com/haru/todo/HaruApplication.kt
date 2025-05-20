package com.haru.todo

import android.app.Application
import com.haru.todo.utils.createNotificationChannel // import 필요
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HaruApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel(this) // ✅ 앱 실행 시 한 번 호출!
    }
}