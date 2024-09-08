package com.food.foodquali

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.food.foodquali.screens.AnalysisScreen
import com.food.foodquali.screens.DashboardScreen
import com.food.foodquali.screens.HistoryScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "dashboard") {
        composable("dashboard") { DashboardScreen(navController) }
        composable("analysis") { AnalysisScreen(navController) }
        composable("history") { HistoryScreen(navController) }
    }
}
