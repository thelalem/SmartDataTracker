package com.example.smartdatatracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable

fun DashboardScreen(){
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Smart Data Tracker",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Monthly Plan: 30GB")
                Text("Used: 0 GB")
                Text("Remaining: 30GB")
            }
        }
    }
}
