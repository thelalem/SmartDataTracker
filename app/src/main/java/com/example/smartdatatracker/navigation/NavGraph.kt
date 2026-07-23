package com.example.smartdatatracker.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.smartdatatracker.ui.screens.DashboardScreen
import com.example.smartdatatracker.ui.screens.UsageHistoryScreen
import com.example.smartdatatracker.ui.screens.SettingsScreen
import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {
    @Serializable
    data object Dashboard : Screen
    
    @Serializable
    data object Usage : Screen
    
    @Serializable
    data object Settings : Screen
}

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard
    ) {
        composable<Screen.Dashboard> {
            DashboardScreen()
        }
        composable<Screen.Usage> {
            UsageHistoryScreen()
        }
        composable<Screen.Settings> {
            SettingsScreen()
        }
    }
}
