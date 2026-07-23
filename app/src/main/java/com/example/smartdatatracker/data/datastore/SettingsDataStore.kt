package com.example.smartdatatracker.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(
    name = "settings"
)

class SettingsDataStore(
    private val context: Context
){
    companion object {
        val MONTHLY_LIMIT = intPreferencesKey("monthly_limit")

        val BILLING_START_DAY = intPreferencesKey("billing_start_day")

        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")

        val DARK_MODE = booleanPreferencesKey("dark_mode")
    }

    suspend fun saveMonthlyLimit(limit: Int) {
        context.dataStore.edit { prefs ->
            prefs[MONTHLY_LIMIT] = limit
        }
    }

    suspend fun saveBillingStartDay(day: Int) {
        context.dataStore.edit { prefs ->
            prefs[BILLING_START_DAY] = day
        }
    }

    suspend fun saveNotificationsEnabled(
        enabled: Boolean
    ) {
        context.dataStore.edit { prefs ->
            prefs[NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun saveDarkMode(
        enabled: Boolean
    ) {
        context.dataStore.edit { prefs ->
            prefs[DARK_MODE] = enabled
        }
    }

    val settingsFlow = context.dataStore.data.map { prefs->
        UserSettings(
            monthlyLimitGB = prefs[MONTHLY_LIMIT] ?: 30,
            billingStartDay = prefs[BILLING_START_DAY] ?: 1,
            notificationsEnabled = prefs[NOTIFICATIONS_ENABLED] ?: true,
            darkMode = prefs[DARK_MODE] ?: false
        )
    }
}