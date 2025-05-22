package com.haru.todo.data.db

import androidx.room.*
import com.haru.todo.data.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    // 1. 활성(숨김 X) 할 일만 조회 (메인/캘린더에서 사용)
    @Query("SELECT * FROM Task WHERE createdDate = :date AND isArchived = 0")
    fun getActiveTasksByDate(date: String): Flow<List<Task>>

    // 2. 모든 할 일(숨김 포함) 조회 (아카이브 관리, 통계 등에서 사용)
    @Query("SELECT * FROM Task WHERE createdDate = :date")
    fun getAllTasksByDate(date: String): Flow<List<Task>>

    // 3. Task 추가/수정/완전삭제 (기존대로)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    // 4. 날짜별 전체 삭제 (관리자/초기화 용)
    @Query("DELETE FROM Task WHERE createdDate = :date")
    suspend fun deleteTasksByDate(date: String)

    // 5. 단일 Task를 "아카이브(숨김)" 처리 (실제 삭제 대신)
    @Query("UPDATE Task SET isArchived = 1 WHERE id = :taskId")
    suspend fun archiveTask(taskId: Int)

    // 6. 숨긴 Task 복원
    @Query("UPDATE Task SET isArchived = 0 WHERE id = :taskId")
    suspend fun restoreTask(taskId: Int)

    // 7. 날짜별 전체 아카이브(숨김) 처리
    @Query("UPDATE Task SET isArchived = 1 WHERE createdDate = :date")
    suspend fun archiveTasksByDate(date: String)
}