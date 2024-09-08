package com.food.foodquali

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.food.foodquali.screens.AnalysisScreen
import com.food.foodquali.screens.DashboardScreen
import com.food.foodquali.screens.HistoryScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun Navigation(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = "dashboard", modifier = modifier) {
        composable("dashboard") { DashboardScreen(navController) }
        composable("analysis") { AnalysisScreen(navController) }
        composable("history") { HistoryScreen(navController) }
    }
}
