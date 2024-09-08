package com.food.foodquali.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.FoodBank
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("Food Quality Assurance", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.FoodBank, // Use the appropriate icon here
                contentDescription = "Food Quality",
                tint = Color.Green, // Optional: change the icon color
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }
            
            Text(
                "Welcome to Food Quality Assurance",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                "Ensure the quality of your food with our advanced analysis tools.",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            ElevatedButton(
                onClick = { navController.navigate("analysis") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(Icons.Default.Analytics, contentDescription = "Analyze")
                Spacer(Modifier.width(8.dp))
                Text("Analyze Food", style = MaterialTheme.typography.titleMedium)
            }
            
            ElevatedButton(
                onClick = { navController.navigate("history") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(Icons.Default.History, contentDescription = "History")
                Spacer(Modifier.width(8.dp))
                Text("View History", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
