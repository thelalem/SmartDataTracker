package com.example.smartdatatracker.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartdatatracker.utils.formatUsage
import com.example.smartdatatracker.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshData()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0A0E1A), Color(0xFF1A1F35))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding() // Fixed notch problem
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            
            // Header
            Text(
                text = "Data Overview",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "${uiState.cycleStartText} - ${uiState.cycleEndText}",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Progress Circle
            UsageProgressCircle(
                percentage = uiState.usagePercentage,
                used = formatUsage(uiState.usedMB),
                total = "${uiState.monthlyLimitGB} GB"
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Main Stats Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DashboardStatCard(
                    modifier = Modifier.weight(1f),
                    title = "Remaining",
                    value = String.format("%.2f GB", uiState.remainingGB),
                    icon = Icons.Default.CloudQueue,
                    color = Color(0xFF6C63FF)
                )
                DashboardStatCard(
                    modifier = Modifier.weight(1f),
                    title = "Today",
                    value = formatUsage(uiState.todayUsageMB),
                    icon = Icons.Default.Today,
                    color = Color(0xFF00C853)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Prediction Card
            DashboardPredictionCard(
                predictedGB = uiState.predictedCycleUsageGB,
                status = uiState.predictionStatus,
                projectedRemaining = uiState.projectedRemainingGB
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Budget Info
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A1F35).copy(alpha = 0.8f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = Color(0xFFFFD93D),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Daily Budget Available",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                        Text(
                            text = String.format("%.2f GB / day", uiState.dailyBudgetGB),
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun UsageProgressCircle(
    percentage: Float,
    used: String,
    total: String
) {
    Box(contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            progress = { percentage.coerceIn(0f, 1f) },
            modifier = Modifier.size(240.dp),
            color = Color(0xFF6C63FF),
            strokeWidth = 12.dp,
            trackColor = Color.White.copy(alpha = 0.1f),
            strokeCap = StrokeCap.Round
        )
        
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = used,
                fontSize = 40.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            Text(
                text = "of $total used",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.6f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Surface(
                shape = CircleShape,
                color = if (percentage > 0.9f) Color(0xFFFF6B6B).copy(alpha = 0.2f) else Color(0xFF00C853).copy(alpha = 0.2f)
            ) {
                Text(
                    text = String.format("%d%%", (percentage * 100).toInt()),
                    color = if (percentage > 0.9f) Color(0xFFFF6B6B) else Color(0xFF00C853),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun DashboardStatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Card(
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1F35).copy(alpha = 0.8f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
            Column {
                Text(
                    text = title,
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
                Text(
                    text = value,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun DashboardPredictionCard(
    predictedGB: Float,
    status: String,
    projectedRemaining: Float
) {
    val isGood = !status.contains("Exceed", ignoreCase = true)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1F35).copy(alpha = 0.8f)
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Forecast",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    shape = CircleShape,
                    color = (if (isGood) Color(0xFF00C853) else Color(0xFFFF6B6B)).copy(alpha = 0.2f)
                ) {
                    Text(
                        text = status,
                        color = if (isGood) Color(0xFF00C853) else Color(0xFFFF6B6B),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Estimated Total",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                    Text(
                        text = String.format("%.2f GB", predictedGB),
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Projected Left",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                    Text(
                        text = String.format("%.2f GB", projectedRemaining),
                        color = if (isGood) Color(0xFF00C853) else Color(0xFFFF6B6B),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
