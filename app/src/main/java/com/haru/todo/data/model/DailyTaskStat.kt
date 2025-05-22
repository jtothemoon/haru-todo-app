package com.haru.todo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_task_stats")
data class DailyTaskStat(
    @PrimaryKey val date: String,      // yyyy-MM-dd (예: 2024-05-22)
    val importantDone: Boolean,        // 중요(1개) 완료: true/false
    val mediumDoneCount: Int,          // 보통(3개) 완료 개수: 0~3
    val generalDoneCount: Int          // 일반(5개) 완료 개수: 0~5
)