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

    fun getTasksByDate(date: String): Flow<List<Task>> =
        taskDao.getTasksByDate(date.toString()) // Room에는 String으로 저장

    suspend fun insertTask(task: Task) = taskDao.insertTask(task)

    suspend fun updateTask(task: Task) = taskDao.updateTask(task)

    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)

    suspend fun deleteTasksByDate(date: String) = taskDao.deleteTasksByDate(date.toString())

    fun getStatsBetween(from: String, to: String): Flow<List<DailyTaskStat>> =
        dailyTaskStatDao.getStatsBetween(from, to)

    // 스냅샷 저장 함수 추가
    suspend fun snapshotDailyStatFor(date: LocalDate) {
        val tasks = taskDao.getTasksByDate(date.toString()).first() // Flow → List
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
