package com.example.smartdatatracker.data.database.dao

import androidx.room.*
import com.example.smartdatatracker.data.database.entities.DailyUsage
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyUsageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyUsage(dailyUsage: DailyUsage)

    @Query("SELECT * FROM daily_usage ORDER BY date DESC")
    fun getAllDailyUsages(): Flow<List<DailyUsage>>

    @Query("SELECT * FROM daily_usage WHERE date = :date LIMIT 1")
    suspend fun getDailyUsageByDate(date: String): DailyUsage?
}