package com.food.foodquali.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun DashboardScreen(navController: NavController) {
    Column {
        Button(onClick = { navController.navigate("analysis") }) {
            Text("Analyze Food")
        }
        Button(onClick = { navController.navigate("history") }) {
            Text("View History")
        }
    }
}
