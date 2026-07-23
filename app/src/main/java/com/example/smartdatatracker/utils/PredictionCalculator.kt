package com.example.smartdatatracker.utils

import com.example.smartdatatracker.data.database.entities.DailyUsage

class PredictionCalculator {

    fun calculateAverageDailyUsage(
        history: List<DailyUsage>
    ): Long {
        if (history.isEmpty()) {
            return 0L
        }

        val totalUsage = history.sumOf {
            it.totalUsageMB
        }

        return totalUsage / history.size
    }

    fun predictCycleUsage(
        currentUsageMB: Long,
        averageDailyUsageMB: Long,
        daysRemaining: Int
    ): Long {
        return currentUsageMB + (averageDailyUsageMB * daysRemaining)
    }

    fun getPredictionStatus(
        predictedUsageMB: Long,
        limitGB: Int
    ): String {
        val limitMB = limitGB * 1024L
        if (limitMB == 0L) return "Set plan limit"

        return when {
            predictedUsageMB > limitMB -> "⚠️ You may exceed your limit"
            predictedUsageMB > limitMB * 0.8 -> "⚠️ High usage"
            else -> "✅ You are on track"
        }
    }
}
