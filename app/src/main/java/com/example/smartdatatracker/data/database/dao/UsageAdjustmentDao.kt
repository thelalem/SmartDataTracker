package com.example.smartdatatracker.data.database.dao

import androidx.room.*
import com.example.smartdatatracker.data.database.entities.UsageAdjustment
import kotlinx.coroutines.flow.Flow

@Dao
interface UsageAdjustmentDao {

    @Insert
    suspend fun insertAdjustment(
        adjustment: UsageAdjustment
    )

    @Query("""
        Select * From usage_adjustments
        Order by date DESC
    """)
    fun getAdjustments(): Flow<List<UsageAdjustment>>
}
