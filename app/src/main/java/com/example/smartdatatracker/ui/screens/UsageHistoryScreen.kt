package com.example.smartdatatracker.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.nativeCanvas
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
            // ✅ Correctly typed PrimaryTabRow with custom indicator
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                indicator = {
                    // 'this' is TabIndicatorScope – tabIndicatorOffset(selectedTabIndex: Int) is available
                    Box(
                        modifier = Modifier
                            .tabIndicatorOffset(selectedTab)
                            .height(3.dp)
                            .background(Color(0xFF6C63FF))
                    )
                },
                divider = {} // remove default divider
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

// ---------- Overview Tab ----------
@Composable
fun HistoryOverviewTab(history: List<DailyUsage>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
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
                        icon = Icons.AutoMirrored.Filled.TrendingUp,
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
            if (history.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📈 Usage Trend",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                            Text(
                                text = "Last 7 days",
                                color = Color.White.copy(alpha = 0.4f),
                                fontSize = 11.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            CleanBarChart(history.take(7).reversed())
                        }
                    }
                }
            }
        }

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

// ---------- Summary Card ----------
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

// ---------- Quick Stat Row ----------
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

// ---------- Clean Line Chart ----------
// Smooth animated line/area chart drawn on a Canvas. Sizing comes straight from
// the Canvas's own measured bounds (no hardcoded pixel heights), so it can never
// overflow/clip like a manually-stacked layout can. The curve draws in left-to-right,
// the peak point is highlighted, and gridlines give a readable scale.
@Composable
fun CleanBarChart(data: List<DailyUsage>) {
    if (data.isEmpty()) return

    val maxUsage = (data.maxByOrNull { it.totalUsageMB }?.totalUsageMB ?: 1L).toFloat()
    val minUsage = (data.minByOrNull { it.totalUsageMB }?.totalUsageMB ?: 0L).toFloat()
    val range = (maxUsage - minUsage).coerceAtLeast(1f)
    val peakIndex = data.indexOfFirst { it.totalUsageMB.toFloat() == maxUsage }

    val animProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = "lineChartAnim"
    )

    val lineColor = Color(0xFF6C63FF)
    val peakColor = Color(0xFFFF6B6B)
    val gridColor = Color.White.copy(alpha = 0.06f)
    val peakLabelPaint = remember {
        android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            isAntiAlias = true
            textAlign = android.graphics.Paint.Align.CENTER
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }
    }
    val peakLabelText = formatUsage(maxUsage.toLong())

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 6.dp)
                    .padding(top = 22.dp, bottom = 4.dp)
            ) {
                val w = size.width
                val h = size.height

                // Gridlines for scale reference
                val gridLines = 3
                repeat(gridLines + 1) { i ->
                    val y = h / gridLines * i
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, y),
                        end = Offset(w, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                if (data.size == 1) {
                    val fraction = ((data[0].totalUsageMB.toFloat() - minUsage) / range).coerceIn(0.08f, 1f)
                    val center = Offset(w / 2f, h - h * fraction * animProgress)
                    drawCircle(color = lineColor, radius = 5.dp.toPx(), center = center)
                    return@Canvas
                }

                val stepX = w / (data.size - 1)
                val points = data.mapIndexed { index, day ->
                    val fraction = ((day.totalUsageMB.toFloat() - minUsage) / range).coerceIn(0.08f, 1f)
                    Offset(stepX * index, h - h * fraction)
                }

                val linePath = Path().apply {
                    moveTo(points.first().x, points.first().y)
                    for (i in 0 until points.size - 1) {
                        val p0 = points[i]
                        val p1 = points[i + 1]
                        val midX = (p0.x + p1.x) / 2f
                        cubicTo(midX, p0.y, midX, p1.y, p1.x, p1.y)
                    }
                }

                // Reveal the curve left-to-right as it animates in
                clipRect(left = 0f, top = 0f, right = w * animProgress, bottom = h) {
                    val fillPath = Path().apply {
                        addPath(linePath)
                        lineTo(points.last().x, h)
                        lineTo(points.first().x, h)
                        close()
                    }
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(lineColor.copy(alpha = 0.35f), lineColor.copy(alpha = 0f))
                        )
                    )
                    drawPath(
                        path = linePath,
                        color = lineColor,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                    points.forEachIndexed { index, p ->
                        val isPeak = index == peakIndex
                        drawCircle(
                            color = if (isPeak) peakColor else lineColor,
                            radius = if (isPeak) 5.dp.toPx() else 3.dp.toPx(),
                            center = p
                        )
                        drawCircle(
                            color = Color(0xFF1A1F35),
                            radius = if (isPeak) 2.2.dp.toPx() else 1.3.dp.toPx(),
                            center = p
                        )
                        if (isPeak) {
                            peakLabelPaint.textSize = 11.sp.toPx()
                            drawContext.canvas.ginativeCanvas.drawText(
                                peakLabelText,
                                p.x,
                                p.y - 12.dp.toPx(),
                                peakLabelPaint
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            data.forEach { day ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = SimpleDateFormat("dd", Locale.US).format(Date(day.date)),
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1
                    )
                    Text(
                        text = SimpleDateFormat("EEE", Locale.US).format(Date(day.date)),
                        color = Color.White.copy(alpha = 0.3f),
                        fontSize = 8.sp,
                        maxLines = 1
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatUsage(minUsage.toLong()),
                color = Color.White.copy(alpha = 0.25f),
                fontSize = 8.sp
            )
            Text(
                text = formatUsage(maxUsage.toLong()),
                color = Color.White.copy(alpha = 0.25f),
                fontSize = 8.sp
            )
        }
    }
}

// ---------- History Records Tab ----------
@Composable
fun HistoryRecordsTab(history: List<DailyUsage>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(history) { entry ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically(
                    initialOffsetY = { it / 2 }
                )
            ) {
                HistoryRecordItem(entry)
            }
        }
    }
}

// ---------- Record Item ----------
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

// ---------- Statistics Tab ----------
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
