package com.example.smartdatatracker.data.repository

import com.example.smartdatatracker.data.datastore.SettingsDataStore

class SettingsRepository (
    private val dataStore: SettingsDataStore
) {
    val settingFlow = dataStore.settingsFlow

    suspend fun saveMonthlyLimit(
        limit: Int
    ){
        dataStore.saveMonthlyLimit(limit)
    }

    suspend fun saveBillingDate(
        date:Int
    ){
        dataStore.saveBillingDate(date)
    }

    suspend fun saveNotification(
        enabled: Boolean
    ){
        dataStore.saveNotificationsEnabled(enabled)
    }

    suspend fun saveDarkMode(
        enabled: Boolean
    ){
        dataStore.saveDarkMode(enabled)
    }

}