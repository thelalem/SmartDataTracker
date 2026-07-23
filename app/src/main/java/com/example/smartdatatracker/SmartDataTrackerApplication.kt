package com.example.smartdatatracker

import android.app.Application
import androidx.work.*
import com.example.smartdatatracker.data.worker.UsageTrackingWorker
import java.util.concurrent.TimeUnit

class SmartDataTrackerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        scheduleUsageTracking()
    }

    private fun scheduleUsageTracking() {
        val request = PeriodicWorkRequestBuilder<UsageTrackingWorker>(
            24,
            TimeUnit.HOURS
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "usage_tracking",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}
