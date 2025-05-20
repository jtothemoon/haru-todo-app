package com.haru.todo.data.db

import androidx.room.*
import com.haru.todo.data.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM Task WHERE createdDate = :date")
    fun getTasksByDate(date: String): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("DELETE FROM Task WHERE createdDate = :date")
    suspend fun deleteTasksByDate(date: String)
}
