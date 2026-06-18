package com.example.smartdatatracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartdatatracker.utils.mbToGb
import com.example.smartdatatracker.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = viewModel()
) {
    val usageList by viewModel.usageData.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
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

        Spacer(modifier = Modifier.height(24.dp))

        // Step 3: Add Test Button
        Button(
            onClick = {
                viewModel.insertTestData()
            }
        ) {
            Text("Insert Test Usage")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Step 4: Display Data
        usageList.forEach { usage ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Phone: ${mbToGb(usage.phoneUsageMB)} GB"
                    )

                    Text(
                        text = "Hotspot: ${mbToGb(usage.hotspotUsageMB)} GB"
                    )

                    Text(
                        text = "Total: ${mbToGb(usage.totalUsageMB)} GB"
                    )
                }
            }
        }
    }
}
