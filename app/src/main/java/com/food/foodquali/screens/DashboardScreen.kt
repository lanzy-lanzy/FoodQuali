package com.food.foodquali.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.BakeryDining
import androidx.compose.material.icons.filled.History
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFFF3E0),  // Light cream
            Color(0xFFFFE0B2)   // Warm peach
        )
    )

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        "Food Quality Assurance",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFF5D4037)  // Deep brown
                    )
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = Color(0xFFFFE0B2).copy(alpha = 0.7f),
                    titleContentColor = Color(0xFF5D4037)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundGradient)
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.BakeryDining,
                contentDescription = "Bakery Quality",
                tint = Color(0xFFD84315),  // Warm orange
                modifier = Modifier.size(120.dp)
            )

            Text(
                "Welcome to Food Quality Assurance",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4E342E),  // Dark brown
                textAlign = TextAlign.Center
            )

            Text(
                "Ensure the quality of your food with our advanced analysis tools.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF6D4C41),  // Medium brown
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            DashboardCard(
                icon = Icons.Default.Analytics,
                title = "Analyze Food Quality Now",
                description = "Start a new quality analysis",
                onClick = { navController.navigate("analysis") }
            )

            DashboardCard(
                icon = Icons.Default.History,
                title = "View History",
                description = "Check your previous analysis results",
                onClick = { navController.navigate("history") }
            )
        }
    }
}

@Composable
fun DashboardCard(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    val cardGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFFF8E1),  // Light wheat
            Color(0xFFFFECB3)   // Golden wheat
        )
    )

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(cardGradient)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFFD84315),  // Warm orange
                modifier = Modifier.size(48.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF4E342E)  // Dark brown
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF6D4C41)  // Medium brown
                )
            }
        }
    }
}