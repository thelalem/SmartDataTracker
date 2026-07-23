package com.example.smartdatatracker.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartdatatracker.data.database.entities.DailyUsage
import com.example.smartdatatracker.utils.formatUsage
import com.example.smartdatatracker.viewmodel.UsageHistoryViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsageHistoryScreen(
    viewModel: UsageHistoryViewModel = viewModel()
) {
    val history by viewModel.history.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Overview", "History", "Stats")

    Scaffold(
        containerColor = Color(0xFF0A0E1A),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "📊 Usage Analytics",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.insertSampleHistory() }) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add test data",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF0A0E1A), Color(0xFF1A1F35))
                    )
                )
        ) {
            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            height = 3.dp,
                            color = Color(0xFF6C63FF)
                        )
                    }
                },
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedTab == index) Color.White else Color.Gray,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                0 -> HistoryOverviewTab(history)
                1 -> HistoryRecordsTab(history)
                2 -> HistoryStatisticsTab(history)
            }
        }
    }
}

@Composable
fun HistoryOverviewTab(history: List<DailyUsage>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Summary Cards
            if (history.isNotEmpty()) {
                val total = history.sumOf { it.totalUsageMB }
                val average = total / history.size
                val highest = history.maxByOrNull { it.totalUsageMB }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HistorySummaryCard(
                        modifier = Modifier.weight(1f),
                        title = "Total",
                        value = formatUsage(total),
                        icon = Icons.Default.DataUsage,
                        color = Color(0xFF6C63FF)
                    )
                    HistorySummaryCard(
                        modifier = Modifier.weight(1f),
                        title = "Avg Daily",
                        value = formatUsage(average),
                        icon = Icons.Default.TrendingUp,
                        color = Color(0xFF00C853)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HistorySummaryCard(
                        modifier = Modifier.weight(1f),
                        title = "Peak Day",
                        value = formatUsage(highest?.totalUsageMB ?: 0),
                        icon = Icons.Default.Whatshot,
                        color = Color(0xFFFF6B6B)
                    )
                    HistorySummaryCard(
                        modifier = Modifier.weight(1f),
                        title = "Days Tracked",
                        value = history.size.toString(),
                        icon = Icons.Default.CalendarToday,
                        color = Color(0xFFFFD93D)
                    )
                }
            }
        }

        item {
            // Chart
            if (history.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1A1F35).copy(alpha = 0.8f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "📈 Usage Trend",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        HistoryModernBarChart(history.take(7).reversed())
                    }
                }
            }
        }

        item {
            // Quick Stats
            if (history.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1A1F35).copy(alpha = 0.8f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "🎯 Quick Stats",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        val stats = listOf(
                            "Highest" to formatUsage(history.maxByOrNull { it.totalUsageMB }?.totalUsageMB ?: 0),
                            "Lowest" to formatUsage(history.minByOrNull { it.totalUsageMB }?.totalUsageMB ?: 0),
                            "Average" to formatUsage(history.map { it.totalUsageMB }.average().toLong())
                        )

                        stats.forEach { (label, value) ->
                            HistoryQuickStatRow(label, value)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistorySummaryCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1F35).copy(alpha = 0.8f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = value,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun HistoryQuickStatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun HistoryModernBarChart(data: List<DailyUsage>) {
    val maxUsage = (data.maxByOrNull { it.totalUsageMB }?.totalUsageMB ?: 1L).coerceAtLeast(1L)
    var animProgress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        animProgress = 1f
    }

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEach { day ->
            val barHeight = (day.totalUsageMB.toFloat() / maxUsage.toFloat() * 150)
            val animatedHeight = barHeight * animProgress

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                // Value label
                Text(
                    text = formatUsage(day.totalUsageMB),
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.width(40.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Bar with gradient
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .height(animatedHeight.dp)
                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = if (day.totalUsageMB == maxUsage) {
                                    listOf(Color(0xFFFF6B6B), Color(0xFFFFD93D))
                                } else {
                                    listOf(Color(0xFF6C63FF), Color(0xFF3D3B8A))
                                }
                            )
                        )
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Date label
                Text(
                    text = SimpleDateFormat("dd", Locale.US).format(Date(day.date)),
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
                Text(
                    text = SimpleDateFormat("EEE", Locale.US).format(Date(day.date)),
                    color = Color.White.copy(alpha = 0.3f),
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun HistoryRecordsTab(history: List<DailyUsage>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(history) { entry ->
            HistoryAnimatedItem {
                HistoryRecordItem(entry)
            }
        }
    }
}

@Composable
fun HistoryAnimatedItem(
    content: @Composable () -> Unit
) {
    val transition = updateTransition(targetState = true, label = "item")
    transition.AnimatedVisibility(
        visible = { it },
        enter = fadeIn() + slideInVertically(
            initialOffsetY = { it / 2 }
        )
    ) {
        content()
    }
}

@Composable
fun HistoryRecordItem(entry: DailyUsage) {
    val dateStr = SimpleDateFormat("EEEE, MMM d", Locale.US).format(Date(entry.date))
    val isHighUsage = entry.totalUsageMB > 500

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1F35).copy(alpha = 0.8f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = dateStr,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isHighUsage) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFF6B6B).copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "🔴 High",
                                color = Color(0xFFFF6B6B),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF00C853).copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "🟢 Normal",
                                color = Color(0xFF00C853),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Text(
                text = formatUsage(entry.totalUsageMB),
                color = Color(0xFF6C63FF),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun HistoryStatisticsTab(history: List<DailyUsage>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            if (history.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1A1F35).copy(alpha = 0.8f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "📊 Detailed Statistics",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(20.dp))

                        val stats = listOf(
                            "Total Usage" to formatUsage(history.sumOf { it.totalUsageMB }),
                            "Average Daily" to formatUsage(history.map { it.totalUsageMB }.average().toLong()),
                            "Highest Day" to formatUsage(history.maxByOrNull { it.totalUsageMB }?.totalUsageMB ?: 0),
                            "Lowest Day" to formatUsage(history.minByOrNull { it.totalUsageMB }?.totalUsageMB ?: 0),
                            "Days Recorded" to history.size.toString(),
                            "Total Data" to "${history.sumOf { it.totalUsageMB } / 1024} GB"
                        )

                        stats.forEachIndexed { index, (label, value) ->
                            if (index > 0) {
                                HorizontalDivider(
                                    color = Color.White.copy(alpha = 0.1f),
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = label,
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = value,
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            if (history.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1A1F35).copy(alpha = 0.8f)
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.DataUsage,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.3f),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No data available",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Tap + to generate test data",
                                color = Color.White.copy(alpha = 0.3f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
