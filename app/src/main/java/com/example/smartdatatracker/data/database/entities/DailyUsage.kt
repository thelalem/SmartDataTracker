package com.example.smartdatatracker.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_usage")
data class DailyUsage(
    @PrimaryKey
    val date: Long,
    val phoneUsageMB: Long,
    val hotspotUsageMB: Long,
    val totalUsageMB: Long
)
