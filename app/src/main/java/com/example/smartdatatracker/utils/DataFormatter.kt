package com.example.smartdatatracker.utils


fun mbToGb(mb: Long): String {
    return String.format("%.2f", mb / 1024.0)
}///