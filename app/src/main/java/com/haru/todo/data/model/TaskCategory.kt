package com.haru.todo.data.model

enum class TaskCategory {
    IMPORTANT, MEDIUM, GENERAL;

    fun displayName(): String = when (this) {
        IMPORTANT -> "중요"
        MEDIUM -> "보통"
        GENERAL -> "일반"
    }
    fun maxCount(): Int = when (this) {
        IMPORTANT -> 1
        MEDIUM -> 3
        GENERAL -> 5
    }
}