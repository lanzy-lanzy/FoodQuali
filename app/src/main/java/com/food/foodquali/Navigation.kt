package com.food.foodquali

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.food.foodquali.screens.AnalysisScreen
import com.food.foodquali.screens.DashboardScreen
import com.food.foodquali.screens.HistoryScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
  import android.os.Build
  import androidx.annotation.RequiresApi
  import androidx.compose.animation.ExperimentalAnimationApi
  import androidx.compose.animation.core.tween
  import androidx.compose.animation.fadeIn
  import androidx.compose.animation.fadeOut
  import androidx.compose.runtime.Composable
  import androidx.compose.ui.Modifier
  import com.food.foodquali.screens.AnalysisScreen
  import com.food.foodquali.screens.DashboardScreen
  import com.food.foodquali.screens.HistoryScreen
  import androidx.navigation.NavHostController
  import androidx.navigation.compose.NavHost
  import androidx.navigation.compose.composable
  import androidx.compose.material3.Icon
  import androidx.compose.material3.NavigationBar
  import androidx.compose.material3.NavigationBarItem
  import androidx.compose.material3.Text
  import androidx.compose.runtime.getValue
  import androidx.navigation.compose.currentBackStackEntryAsState
  import androidx.compose.material3.Scaffold
  import androidx.compose.ui.unit.dp
  import androidx.compose.foundation.layout.padding
  import androidx.compose.material.icons.Icons
  import androidx.compose.material.icons.filled.Dashboard
  import androidx.compose.material.icons.filled.Analytics
  import androidx.compose.material.icons.filled.History

  @RequiresApi(Build.VERSION_CODES.P)
  @Composable
  fun Navigation(navController: NavHostController, modifier: Modifier = Modifier) {
      val navBackStackEntry by navController.currentBackStackEntryAsState()
      val currentRoute = navBackStackEntry?.destination?.route

      Scaffold(
          bottomBar = {
              NavigationBar {
                  NavigationBarItem(
                      icon = { Icon(Icons.Filled.Dashboard, contentDescription = "Dashboard") },
                      label = { Text("Dashboard") },
                      selected = currentRoute == "dashboard",
                      onClick = { navController.navigate("dashboard") }
                  )
                  NavigationBarItem(
                      icon = { Icon(Icons.Filled.Analytics, contentDescription = "Analysis") },
                      label = { Text("Analysis") },
                      selected = currentRoute == "analysis",
                      onClick = { navController.navigate("analysis") }
                  )
                  NavigationBarItem(
                      icon = { Icon(Icons.Filled.History, contentDescription = "History") },
                      label = { Text("History") },
                      selected = currentRoute == "history",
                      onClick = { navController.navigate("history") }
                  )
              }
          }
      ) { innerPadding ->
          NavHost(
              navController = navController,
              startDestination = "dashboard",
              modifier = modifier.padding(innerPadding),
              enterTransition = { fadeIn(animationSpec = tween(300)) },
              exitTransition = { fadeOut(animationSpec = tween(300)) }
          ) {
              composable("dashboard") { DashboardScreen(navController) }
              composable("analysis") { AnalysisScreen(navController) }
              composable("history") { HistoryScreen(navController) }
          }
      }
  }