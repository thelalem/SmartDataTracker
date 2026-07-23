package com.example.smartdatatracker.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.smartdatatracker.data.database.DatabaseProvider
import com.example.smartdatatracker.data.database.entities.DailyUsage
import com.example.smartdatatracker.data.usage.UsageCollector
import com.example.smartdatatracker.utils.getStartOfToday

class UsageTrackingWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val database = DatabaseProvider.getDatabase(applicationContext)
        val dao = database.dailyUsageDao()
        val usageCollector = UsageCollector(applicationContext)
        val today = getStartOfToday()

        val usageMB = usageCollector.getTodayMobileUsageMB()

        dao.insertDailyUsage(
            DailyUsage(
                date = today,
                phoneUsageMB = usageMB,
                hotspotUsageMB = 0,
                totalUsageMB = usageMB
            )
        )

        return Result.success()
    }
}
