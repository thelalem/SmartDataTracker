package com.example.smartdatatracker.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartdatatracker.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory)
) {
    // Collecting state from the ViewModel
    val settings by settingsViewModel.settings.collectAsState()

    // Using Column for vertical layout
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Monthly Limit: ${settings?.monthlyLimitGB ?: "30"} GB")

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                settingsViewModel.saveMonthlyLimit(50)
            }
        ) {
            Text("Set 50 GB Limit")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Current Value: ${settings?.monthlyLimitGB ?: "--"} GB",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
