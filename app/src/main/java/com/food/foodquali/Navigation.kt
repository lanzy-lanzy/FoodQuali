package com.food.foodquali

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import com.food.foodquali.screens.AnalysisScreen
import com.food.foodquali.screens.DashboardScreen
import com.food.foodquali.screens.HistoryScreen

import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Navigation() {
    val navController = rememberAnimatedNavController()
    AnimatedNavHost(navController = navController, startDestination = "dashboard") {
        composable("dashboard") { DashboardScreen(navController) }
        composable("analysis") { AnalysisScreen(navController) }
        composable("history") { HistoryScreen(navController) }
    }
}
