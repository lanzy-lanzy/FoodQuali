package com.food.foodquali

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController


@RequiresApi(Build.VERSION_CODES.P)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFFF5E0B3), // Warm, light wheat color
                contentColor = Color(0xFF8B4513)    // Saddle brown for text and icons
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                listOf(
                    Screen.Dashboard,
                    Screen.Analysis,
                    Screen.History
                ).forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.route) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFD84315),    // Warm orange for selected icon
                            selectedTextColor = Color(0xFFD84315),    // Warm orange for selected text
                            unselectedIconColor = Color(0xFF8B4513),  // Saddle brown for unselected icon
                            unselectedTextColor = Color(0xFF8B4513),  // Saddle brown for unselected text
                            indicatorColor = Color(0xFFFFE0B2)        // Light peach for the selection indicator
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Navigation(navController, Modifier.padding(innerPadding))
    }
}
sealed class Screen(val route: String, val icon: ImageVector) {
    object Dashboard : Screen("Dashboard", Icons.Filled.Dashboard)
    object Analysis : Screen("Analysis", Icons.Filled.Analytics)
    object History : Screen("History", Icons.Filled.History)
}
