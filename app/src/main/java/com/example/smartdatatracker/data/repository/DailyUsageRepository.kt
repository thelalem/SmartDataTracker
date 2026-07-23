package com.example.smartdatatracker.data.repository

import com.example.smartdatatracker.data.database.dao.DailyUsageDao
import com.example.smartdatatracker.data.database.entities.DailyUsage

class DailyUsageRepository(
    private val dailyUsageDao: DailyUsageDao
) {

    fun getAllDailyUsages() = dailyUsageDao.getAllDailyUsages()

    suspend fun insertDailyUsage(dailyUsage: DailyUsage) {
        dailyUsageDao.insertDailyUsage(dailyUsage)
    }

    suspend fun getDailyUsageByDate(date: Long): DailyUsage? {
        return dailyUsageDao.getDailyUsageByDate(date)
    }

    suspend fun getDailyUsageList(): List<DailyUsage> {
        return dailyUsageDao.getDailyUsageList()
    }
}
