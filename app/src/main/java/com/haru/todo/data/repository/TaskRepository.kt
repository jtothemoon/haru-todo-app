package com.haru.todo.data.repository

import com.haru.todo.data.db.TaskDao
import com.haru.todo.data.model.Task
import kotlinx.coroutines.flow.Flow

class TaskRepository(
    private val taskDao: TaskDao
) {

    fun getTasksByDate(date: String): Flow<List<Task>> =
        taskDao.getTasksByDate(date.toString()) // Room에는 String으로 저장

    suspend fun insertTask(task: Task) = taskDao.insertTask(task)

    suspend fun updateTask(task: Task) = taskDao.updateTask(task)

    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)

    suspend fun deleteTasksByDate(date: String) = taskDao.deleteTasksByDate(date.toString())
}
