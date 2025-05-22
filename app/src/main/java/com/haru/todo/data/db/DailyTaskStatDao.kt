package com.haru.todo.data.db

import androidx.room.*
import com.haru.todo.data.model.DailyTaskStat
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyTaskStatDao {
    @Query("SELECT * FROM daily_task_stats WHERE date = :date")
    suspend fun getStatByDate(date: String): DailyTaskStat?

    @Query("SELECT * FROM daily_task_stats WHERE date BETWEEN :from AND :to")
    fun getStatsBetween(from: String, to: String): Flow<List<DailyTaskStat>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStat(stat: DailyTaskStat)
}
