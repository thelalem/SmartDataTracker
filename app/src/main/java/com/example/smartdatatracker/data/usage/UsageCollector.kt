package com.example.smartdatatracker.data.usage

import android.app.AppOpsManager
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.os.Process
import android.content.Intent
import android.net.ConnectivityManager
import android.provider.Settings
import java.util.Calendar

class UsageCollector(
    private val context: Context
) {
    private val networkStatsManager =
        context.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager

    fun getTodayMobileUsageMB(): Long {
        return getMobileUsageBetween(getStartOfDay())
    }

    fun getMobileUsageBetween(
        startTime: Long,
        endTime: Long = System.currentTimeMillis()
    ): Long {
        val networkStats = try {
            networkStatsManager.querySummary(
                ConnectivityManager.TYPE_MOBILE,
                null, // Passing null is standard for non-system apps
                startTime,
                endTime
            )
        } catch (e: SecurityException) {
            return -1L // Signal error or missing permission
        }

        var totalBytes = 0L
        val bucket = android.app.usage.NetworkStats.Bucket()

        while (networkStats.hasNextBucket()) {
            networkStats.getNextBucket(bucket)
            totalBytes += bucket.rxBytes
            totalBytes += bucket.txBytes
        }

        networkStats.close()
        return totalBytes / (1024 * 1024)
    }

    private fun getStartOfDay(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    fun hasUsageAccess(): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    fun openUsageAccessSettings() {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }
}
