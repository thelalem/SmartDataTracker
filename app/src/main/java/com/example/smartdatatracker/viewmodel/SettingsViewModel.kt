package com.example.smartdatatracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.smartdatatracker.data.datastore.SettingsDataStore
import com.example.smartdatatracker.data.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: SettingsRepository
) : ViewModel() {

    val settings = repository.settingFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    fun saveMonthlyLimit(limit: Int) {
        viewModelScope.launch {
            repository.saveMonthlyLimit(limit)
        }
    }

    fun saveBillingDate(date: Int) {
        viewModelScope.launch {
            repository.saveBillingDate(date)
        }
    }

    fun saveDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            repository.saveDarkMode(enabled)
        }
    }

    fun saveNotification(enabled: Boolean) {
        viewModelScope.launch {
            repository.saveNotification(enabled)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as android.app.Application)
                val dataStore = SettingsDataStore(application.applicationContext)
                val repository = SettingsRepository(dataStore)
                SettingsViewModel(repository)
            }
        }
    }
}
