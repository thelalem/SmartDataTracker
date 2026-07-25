package com.example.smartdatatracker.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.smartdatatracker.data.database.DatabaseProvider
import com.example.smartdatatracker.data.database.entities.DailyUsage
import com.example.smartdatatracker.data.usage.UsageCollector
import java.util.Calendar

class UsageTrackingWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val database = DatabaseProvider.getDatabase(applicationContext)
        val dao = database.dailyUsageDao()
        val usageCollector = UsageCollector(applicationContext)

        // Self-healing: Check last 7 days for missing entries
        val calendar = Calendar.getInstance()
        repeat(7) { i ->
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            
            // Reset to exact start of that day
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            
            val dayStart = calendar.timeInMillis
            val dayEnd = dayStart + (24 * 60 * 60 * 1000) - 1

            // If we don't have data for this day yet, or if it's "today" (to keep it updated)
            // Note: Since 'date' is PrimaryKey, we check existence
            val existing = dao.getDailyUsageByDate(dayStart)
            if (existing == null || i == 0) {
                val usageMB = usageCollector.getMobileUsageBetween(dayStart, dayEnd)
                if (usageMB >= 0) {
                    dao.insertDailyUsage(
                        DailyUsage(
                            date = dayStart,
                            phoneUsageMB = usageMB,
                            hotspotUsageMB = 0,
                            totalUsageMB = usageMB
                        )
                    )
                }
            }
        }

        return Result.success()
    }
}
