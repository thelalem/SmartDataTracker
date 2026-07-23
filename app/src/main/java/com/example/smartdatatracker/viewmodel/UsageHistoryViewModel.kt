package com.example.smartdatatracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartdatatracker.data.database.DatabaseProvider
import com.example.smartdatatracker.data.database.entities.DailyUsage
import com.example.smartdatatracker.data.repository.DailyUsageRepository
import com.example.smartdatatracker.data.usage.UsageCollector
import com.example.smartdatatracker.utils.getStartOfToday
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class UsageHistoryViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository: DailyUsageRepository
    private val usageCollector = UsageCollector(application)

    val history: StateFlow<List<DailyUsage>>

    init {
        val db = DatabaseProvider.getDatabase(application)
        repository = DailyUsageRepository(db.dailyUsageDao())

        history = repository.getAllDailyUsages()
            .map { list ->
                val today = getStartOfToday()
                val todayUsageMB = usageCollector.getTodayMobileUsageMB()
                val todayEntry = DailyUsage(
                    date = today,
                    phoneUsageMB = todayUsageMB,
                    hotspotUsageMB = 0,
                    totalUsageMB = todayUsageMB
                )

                // Filter out any existing database entry for today and prepend the live one
                val filteredList = list.filter { it.date != today }
                listOf(todayEntry) + filteredList
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )
    }

    fun insertSampleHistory() {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            // Reset to midnight
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            val samples = listOf(
                600L, 800L, 400L, 1200L, 300L, 900L, 150L
            )

            samples.reversed().forEachIndexed { index, usage ->
                val dayCalendar = calendar.clone() as Calendar
                dayCalendar.add(Calendar.DAY_OF_YEAR, -index)
                
                repository.insertDailyUsage(
                    DailyUsage(
                        date = dayCalendar.timeInMillis,
                        phoneUsageMB = usage,
                        hotspotUsageMB = 0,
                        totalUsageMB = usage
                    )
                )
            }
        }
    }
}
