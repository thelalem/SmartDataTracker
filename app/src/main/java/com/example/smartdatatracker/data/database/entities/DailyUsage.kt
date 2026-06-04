package com.example.smartdatatracker.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_usage")
data class DailyUsage(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: Long,
    val phoneUsageGB: Long,
    val hotspotUsageGB: Long,
    val totalUsageGB: Long
)
