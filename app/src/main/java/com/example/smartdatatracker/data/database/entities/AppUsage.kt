package com.example.smartdatatracker.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "app_usage")
data class AppUsage (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: Long,
    val packageName: String,
    val appName: String,
    val usageMB: Long

)