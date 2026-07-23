package com.example.smartdatatracker.utils

fun formatUsage(mb: Long): String {
    return if (mb >= 1024) {
        String.format("%.2f GB", mb / 1024.0)
    } else {
        "$mb MB"
    }
}

fun mbToGb(mb: Long): String {
    return String.format("%.2f", mb / 1024.0)
}
