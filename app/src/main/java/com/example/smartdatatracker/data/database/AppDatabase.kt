package com.example.smartdatatracker.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.smartdatatracker.data.database.dao.AppUsageDao
import com.example.smartdatatracker.data.database.dao.DailyUsageDao
import com.example.smartdatatracker.data.database.dao.UsageAdjustmentDao
import com.example.smartdatatracker.data.database.entities.AppUsage
import com.example.smartdatatracker.data.database.entities.DailyUsage
import com.example.smartdatatracker.data.database.entities.UsageAdjustment


@Database(
    entities = [
        DailyUsage::class,
        AppUsage::class,
        UsageAdjustment::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dailyUsageDao(): DailyUsageDao
    abstract fun appUsageDao(): AppUsageDao
    abstract fun usageAdjustmentDao(): UsageAdjustmentDao
}
