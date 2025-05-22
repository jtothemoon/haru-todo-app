package com.haru.todo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val isDone: Boolean = false,
    val category: TaskCategory,
    val createdDate: LocalDate, // 오늘 날짜 기준 관리 (추후 TypeConverter 필요)
    val isArchived: Boolean = false // 아카이브/비활성화 플래그
)
