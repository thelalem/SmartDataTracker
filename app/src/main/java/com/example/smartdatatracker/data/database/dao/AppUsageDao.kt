package com.example.smartdatatracker.data.database.dao

import androidx.room.*
import com.example.smartdatatracker.data.database.entities.AppUsage
import kotlinx.coroutines.flow.Flow

@Dao
interface AppUsageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppUsage(appUsage: AppUsage)

    @Query("""
        Select * from app_usage
        Order by usageMB DESC
    """)
    fun getAllApps(): Flow<List<AppUsage>>
}