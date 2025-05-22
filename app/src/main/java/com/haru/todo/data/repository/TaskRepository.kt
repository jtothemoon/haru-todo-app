package com.haru.todo.data.repository

import com.haru.todo.data.db.DailyTaskStatDao
import com.haru.todo.data.db.TaskDao
import com.haru.todo.data.model.DailyTaskStat
import com.haru.todo.data.model.Task
import com.haru.todo.data.model.TaskCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.time.LocalDate

class TaskRepository(
    private val taskDao: TaskDao,
    private val dailyTaskStatDao: DailyTaskStatDao
) {
    // 1. 활성(숨김X) 할 일만 조회 (메인/캘린더)
    fun getActiveTasksByDate(date: String): Flow<List<Task>> =
        taskDao.getActiveTasksByDate(date)

    // 2. 모든 할 일(숨김 포함) 조회 (아카이브 관리/통계)
    fun getAllTasksByDate(date: String): Flow<List<Task>> =
        taskDao.getAllTasksByDate(date)

    // 3. 할 일 추가/수정
    suspend fun insertTask(task: Task) = taskDao.insertTask(task)
    suspend fun updateTask(task: Task) = taskDao.updateTask(task)

    // 4. 할 일 숨기기(삭제 → 숨김)
    suspend fun archiveTask(taskId: Int) = taskDao.archiveTask(taskId)

    // 5. 숨긴 할 일 복원
    suspend fun restoreTask(taskId: Int) = taskDao.restoreTask(taskId)

    // 6. 완전 삭제 (아카이브 관리화면 등)
    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)

    // 7. 날짜별 전체 삭제(관리자/초기화 용)
    suspend fun deleteTasksByDate(date: String) = taskDao.deleteTasksByDate(date)

    suspend fun archiveTasksByDate(date: String) = taskDao.archiveTasksByDate(date)

    // 8. 통계용 (변경 없음)
    fun getStatsBetween(from: String, to: String): Flow<List<DailyTaskStat>> =
        dailyTaskStatDao.getStatsBetween(from, to)

    // 9. 스냅샷 저장 함수 (isArchived = 0만 통계에 포함하려면 필터 추가)
    suspend fun snapshotDailyStatFor(date: LocalDate) {
        val tasks = taskDao.getActiveTasksByDate(date.toString()).first() // 활성만 통계
        val importantDone = tasks.any { it.category == TaskCategory.IMPORTANT && it.isDone }
        val mediumDoneCount = tasks.count { it.category == TaskCategory.MEDIUM && it.isDone }
        val generalDoneCount = tasks.count { it.category == TaskCategory.GENERAL && it.isDone }

        val stat = DailyTaskStat(
            date = date.toString(),
            importantDone = importantDone,
            mediumDoneCount = mediumDoneCount,
            generalDoneCount = generalDoneCount
        )
        dailyTaskStatDao.upsertStat(stat)
    }
}