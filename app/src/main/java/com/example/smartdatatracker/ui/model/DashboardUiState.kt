package com.example.smartdatatracker.ui.model

data class DashboardUiState(
    val usedMB: Long = 0,
    val usedGB: Float = 0f,
    val monthlyLimitGB: Int = 30,
    val remainingGB: Float = 30f,
    val usagePercentage: Float = 0f,
    val todayUsageMB: Long = 0,
    val daysRemaining: Int = 0,
    val cycleStartText: String = "",
    val cycleEndText: String = "",
    val averageDailyUsageMB: Long = 0,
    val predictedCycleUsageGB: Float = 0f,
    val predictionMessage: String = "",
    val predictionStatus: String = "",
    val projectedRemainingGB: Float = 0f,
    val dailyBudgetGB: Float = 0f
)
