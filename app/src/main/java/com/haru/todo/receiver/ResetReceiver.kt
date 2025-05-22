package com.haru.todo.receiver

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.datastore.preferences.core.edit
import com.haru.todo.MainActivity
import com.haru.todo.R
import com.haru.todo.data.db.AppDatabase
import com.haru.todo.data.repository.TaskRepository
import com.haru.todo.utils.AlarmScheduler
import com.haru.todo.utils.dataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

class ResetReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        when(intent?.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                // 👇 마지막에 사용자가 지정한 초기화 시각을 DataStore 등에서 불러온다
                // (DataStore에서 시간 읽어서 알람 재등록)
                CoroutineScope(Dispatchers.IO).launch {
                    val prefs = context.dataStore.data.first()
                    val hour = prefs[ResetPrefs.HOUR_KEY] ?: 0
                    val minute = prefs[ResetPrefs.MINUTE_KEY] ?: 0
                    // 알람 재등록
                    AlarmScheduler.scheduleResetAlarm(context, hour, minute)
                }
            }

            else -> {
                Log.d("ResetReceiver", "알람이 실행되었습니다! DB 초기화 시작!")
                CoroutineScope(Dispatchers.IO).launch {
                    val db = AppDatabase.getInstance(context)
                    val repository = TaskRepository(db.taskDao(), db.dailyTaskStatDao())
                    val yesterday = LocalDate.now().minusDays(1)

                    // 1. 어제 할 일 통계 스냅샷 저장
                    repository.snapshotDailyStatFor(yesterday)

                    // 2. 오늘 할 일 초기화
                    db.taskDao().archiveTasksByDate(LocalDate.now().toString())

                    // 3. DataStore에 리셋 시각 기록
                    context.dataStore.edit { prefs ->
                        prefs[ResetPrefs.LAST_RESET_TIME] = System.currentTimeMillis()
                    }
                    Log.d("ResetReceiver", "DB 초기화 완료!")
                }

                // 🔔 알림 띄우기 (IO 코루틴 밖, 즉시 실행)
                showResetNotification(context)
            }
        }
    }

    private fun showResetNotification(context: Context) {
        // DataStore에서 알림 허용값 읽어서 처리!
        CoroutineScope(Dispatchers.IO).launch {
            val prefs = context.dataStore.data.first()
            val allow = prefs[ResetPrefs.ALLOW_NOTIFICATION] ?: true
            if (!allow) {
                Log.d("ResetReceiver", "알림 설정 꺼져있어 미발송")
                return@launch
            }
            // 알림은 메인스레드에서 발송!
            kotlinx.coroutines.withContext(Dispatchers.Main) {
                val intent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                val pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val builder = NotificationCompat.Builder(context, "reset_channel")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("오늘의 할 일 초기화")
                    .setContentText("하루의 할 일이 모두 초기화되었습니다!")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)

                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED
                    || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
                ) {
                    with(NotificationManagerCompat.from(context)) {
                        notify(135, builder.build())
                        Log.d("ResetReceiver", "알림 발송 성공!")
                    }
                } else {
                    Log.d("ResetReceiver", "알림 권한 없음")
                }
            }
        }
    }

}
