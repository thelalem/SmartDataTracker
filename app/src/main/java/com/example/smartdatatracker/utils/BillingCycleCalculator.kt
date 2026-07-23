package com.example.smartdatatracker.utils

import android.icu.util.Calendar

class BillingCycleCalculator {
    fun getCycleStartDate(
        billingStartDay: Int,
        currentTime: Long = System.currentTimeMillis()
    ): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentTime
        val today = calendar.get(Calendar.DAY_OF_MONTH)

        if (today >= billingStartDay) {
            val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            calendar.set(Calendar.DAY_OF_MONTH, billingStartDay.coerceAtMost(maxDay))
        } else {
            calendar.add(Calendar.MONTH, -1)
            val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            calendar.set(Calendar.DAY_OF_MONTH, billingStartDay.coerceAtMost(maxDay))
        }

        resetTime(calendar)

        return calendar.timeInMillis
    }

    fun getCycleEndDate(
        billingStartDay: Int,
        currentTime: Long = System.currentTimeMillis()
    ): Long {
        val startCalendar = Calendar.getInstance()
        startCalendar.timeInMillis = getCycleStartDate(billingStartDay, currentTime)
        startCalendar.add(Calendar.MONTH, 1)
        startCalendar.add(Calendar.DAY_OF_MONTH, -1)
        resetTime(startCalendar)
        return startCalendar.timeInMillis
    }


    fun getDaysRemaining(
        billingStartDay: Int,
        currentTime: Long = System.currentTimeMillis()
    ): Int {
        val endDate = getCycleEndDate(billingStartDay, currentTime)
        val diff = endDate - currentTime
        return (diff / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(0)
    }

    private fun resetTime(calendar: Calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
    }
}
