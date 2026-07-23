package com.example.smartdatatracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartdatatracker.data.database.DatabaseProvider
import com.example.smartdatatracker.data.database.entities.DailyUsage
import com.example.smartdatatracker.data.datastore.SettingsDataStore
import com.example.smartdatatracker.data.repository.DailyUsageRepository
import com.example.smartdatatracker.data.repository.SettingsRepository
import com.example.smartdatatracker.data.usage.UsageCollector
import com.example.smartdatatracker.ui.model.DashboardUiState
import com.example.smartdatatracker.utils.BillingCycleCalculator
import com.example.smartdatatracker.utils.PredictionCalculator
import com.example.smartdatatracker.utils.getStartOfToday
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DashboardViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val repository: DailyUsageRepository
    private val settingsRepository: SettingsRepository
    private val usageCollector = UsageCollector(application)
    private val billingCalculator = BillingCycleCalculator()
    private val predictionCalculator = PredictionCalculator()

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState = _uiState.asStateFlow()

    val usageData: StateFlow<List<DailyUsage>>

    init {
        val db = DatabaseProvider.getDatabase(application)
        repository = DailyUsageRepository(db.dailyUsageDao())
        
        val dataStore = SettingsDataStore(application)
        settingsRepository = SettingsRepository(dataStore)

        usageData = repository.getAllDailyUsages().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        // Observe settings and usage data reactively
        viewModelScope.launch {
            settingsRepository.settingFlow.combine(usageData) { settings, history ->
                settings to history
            }.collect { (settings, history) ->
                refreshWithSettings(settings, history)
            }
        }
    }

    private suspend fun refreshWithSettings(
        settings: com.example.smartdatatracker.data.datastore.UserSettings,
        history: List<DailyUsage>
    ) {
        android.util.Log.d("DashboardViewModel", "Refreshing with settings: $settings")
        val todayUsage = usageCollector.getTodayMobileUsageMB()
        
        val start = billingCalculator.getCycleStartDate(settings.billingStartDay)
        val end = billingCalculator.getCycleEndDate(settings.billingStartDay)
        val usedMB = usageCollector.getMobileUsageBetween(start)
        
        val usedGB = usedMB / 1024f
        val limit = settings.monthlyLimitGB
        val remainingGB = (limit - usedGB).coerceAtLeast(0f)
        val percent = if (limit > 0) (usedGB / limit) else 0f
        
        val daysRemaining = billingCalculator.getDaysRemaining(settings.billingStartDay)
        
        // Prediction Logic
        val averageDailyMB = predictionCalculator.calculateAverageDailyUsage(history)
        val predictedMB = predictionCalculator.predictCycleUsage(usedMB, averageDailyMB, daysRemaining)
        val predictedGB = predictedMB / 1024f
        
        val status = predictionCalculator.getPredictionStatus(predictedMB, limit)
        val projectedRemaining = (limit - predictedGB).coerceAtLeast(0f)
        
        // Daily budget available
        val dailyBudget = if (daysRemaining > 0) remainingGB / daysRemaining else remainingGB

        val dateFormat = SimpleDateFormat("MMM dd", Locale.US)
        
        _uiState.update {
            it.copy(
                usedMB = usedMB,
                usedGB = usedGB,
                monthlyLimitGB = limit,
                remainingGB = remainingGB,
                usagePercentage = percent,
                todayUsageMB = todayUsage,
                daysRemaining = daysRemaining,
                cycleStartText = dateFormat.format(Date(start)),
                cycleEndText = dateFormat.format(Date(end)),
                averageDailyUsageMB = averageDailyMB,
                predictedCycleUsageGB = predictedGB,
                predictionStatus = status,
                projectedRemainingGB = projectedRemaining,
                dailyBudgetGB = dailyBudget
            )
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            val settings = settingsRepository.settingFlow.first()
            val history = usageData.value
            refreshWithSettings(settings, history)
        }
    }

    fun loadTodayUsage() = refreshData()
    fun loadCycleUsage() = refreshData()

    fun insertTestData() {
        viewModelScope.launch {
            repository.insertDailyUsage(
                DailyUsage(
                    date = getStartOfToday(),
                    phoneUsageMB = 500,
                    hotspotUsageMB = 100,
                    totalUsageMB = 600
                )
            )
            refreshData()
        }
    }
}
