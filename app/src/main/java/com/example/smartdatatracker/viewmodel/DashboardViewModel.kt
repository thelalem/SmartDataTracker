package com.example.smartdatatracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Database
import com.example.smartdatatracker.data.database.DatabaseProvider
import com.example.smartdatatracker.data.database.entities.DailyUsage
import com.example.smartdatatracker.data.repository.DailyUsageRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DashboardViewModel (
    application: Application
) : AndroidViewModel(application) {
    private val repository: DailyUsageRepository

    val usageData: StateFlow<List<DailyUsage>>

    init {
        val db = DatabaseProvider.getDatabase(application)

        repository = DailyUsageRepository(
            db.dailyUsageDao()
        )

        usageData = repository.getAllDailyUsages().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
    }

    fun insertTestData(){

        viewModelScope.launch {
            repository.insertDailyUsage(
                DailyUsage(
                    date = System.currentTimeMillis(),
                    phoneUsageMB = 5000,
                    hotspotUsageMB = 2000,
                    totalUsageMB = 7000
                )
            )
        }//
    }
}