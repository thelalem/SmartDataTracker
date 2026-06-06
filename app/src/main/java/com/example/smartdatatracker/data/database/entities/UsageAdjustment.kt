package com.example.smartdatatracker.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usage_adjustments")
data class UsageAdjustment(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: Long,
    val adjustmentGB: Long,
    val adjustmentReason: String //hela
)
