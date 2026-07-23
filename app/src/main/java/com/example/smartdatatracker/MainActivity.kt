package com.example.smartdatatracker

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.smartdatatracker.data.usage.UsageCollector
import com.example.smartdatatracker.navigation.AppNavHost
import com.example.smartdatatracker.navigation.Screen
import com.example.smartdatatracker.ui.theme.SmartDataTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1. Enable Edge-to-Edge properly
        enableEdgeToEdge()
        
        val collector = UsageCollector(this)
        if (!collector.hasUsageAccess()) {
            collector.openUsageAccessSettings()
        }

        setContent {
            SmartDataTrackerTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                // 2. Main Box with the full-screen gradient background
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF0A0E1A),
                                    Color(0xFF1A1F35),
                                    Color(0xFF0D1120)
                                )
                            )
                        )
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = Color.Transparent, // Transparent Scaffold
                        contentWindowInsets = WindowInsets(0, 0, 0, 0), // No auto-padding for insets
                        bottomBar = {
                            ModernBottomNavigation(
                                currentDestination = currentDestination,
                                onNavigate = { screen ->
                                    navController.navigate(screen) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    ) { innerPadding ->
                        // 3. Application Content
                        Box(
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxSize()
                        ) {
                            AppNavHost(navController = navController)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModernBottomNavigation(
    currentDestination: NavDestination?,
    onNavigate: (Screen) -> Unit
) {
    val items = listOf(
        NavigationItem(
            label = "Dashboard",
            screen = Screen.Dashboard,
            icon = Icons.Filled.Home,
            iconOutlined = Icons.Outlined.Home
        ),
        NavigationItem(
            label = "Usage",
            screen = Screen.Usage,
            icon = Icons.Default.Timeline,
            iconOutlined = Icons.Default.Timeline
        ),
        NavigationItem(
            label = "Settings",
            screen = Screen.Settings,
            icon = Icons.Filled.Settings,
            iconOutlined = Icons.Outlined.Settings
        )
    )

    val selectedIndex = items.indexOfFirst { item ->
        currentDestination?.hierarchy?.any {
            when (item.screen) {
                Screen.Dashboard -> it.hasRoute(Screen.Dashboard::class)
                Screen.Usage -> it.hasRoute(Screen.Usage::class)
                Screen.Settings -> it.hasRoute(Screen.Settings::class)
            }
        } == true
    }.coerceAtLeast(0)

    // Match the reference image 2: Floating capsule
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding() // Space from system bottom bar
            .padding(horizontal = 24.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .height(64.dp)
                .fillMaxWidth()
                .shadow(12.dp, CircleShape),
            shape = CircleShape,
            color = Color(0xFF1E1E1E).copy(alpha = 0.95f), // Dark background like image
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, item ->
                    val isSelected = index == selectedIndex
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(onClick = { onNavigate(item.screen) }),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // Capsule highlight for selected item
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) Color(0xFF6C63FF).copy(alpha = 0.15f) 
                                        else Color.Transparent
                                    )
                                    .padding(horizontal = 16.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isSelected) item.icon else item.iconOutlined,
                                    contentDescription = item.label,
                                    modifier = Modifier.size(24.dp),
                                    tint = if (isSelected) Color(0xFF6C63FF) else Color.White.copy(alpha = 0.6f)
                                )
                            }
                            
                            Text(
                                text = item.label,
                                color = if (isSelected) Color(0xFF6C63FF) else Color.White.copy(alpha = 0.4f),
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

data class NavigationItem(
    val label: String,
    val screen: Screen,
    val icon: ImageVector,
    val iconOutlined: ImageVector
)
