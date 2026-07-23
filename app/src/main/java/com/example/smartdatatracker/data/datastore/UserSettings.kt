package com.example.smartdatatracker.data.datastore

data class UserSettings (
    val monthlyLimitGB: Int = 30,
    val billingStartDay: Int = 1,
    val notificationsEnabled: Boolean = true,
    val darkMode: Boolean = false
)