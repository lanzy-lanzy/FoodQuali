package com.food.foodquali.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.food.foodquali.viewmodels.FoodQualityViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavController) {
    val viewModel: FoodQualityViewModel = viewModel()
    val analysisHistory by viewModel.analysisHistory.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }
    var selectedAnalysis by remember { mutableStateOf<Map<String, Any>?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var analysisToDelete by remember { mutableStateOf<Map<String, Any>?>(null) }

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFFF3E0),  // Light cream
            Color(0xFFFFE0B2)   // Warm peach
        )
    )

    LaunchedEffect(key1 = Unit) {
        viewModel.getFoodAnalysisHistory()
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        "Food Analysis History",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5D4037)  // Deep brown
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF5D4037)  // Deep brown
                        )
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = Color(0xFFFFE0B2).copy(alpha = 0.7f),
                    titleContentColor = Color(0xFF5D4037)
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundGradient)
        ) {
            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing),
                onRefresh = {
                    isRefreshing = true
                    viewModel.getFoodAnalysisHistory()
                    isRefreshing = false
                },
                modifier = Modifier.fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    items(analysisHistory.reversed()) { analysis ->
                        HistoryItem(
                            analysis = analysis,
                            onClick = { selectedAnalysis = analysis },
                            onDelete = {
                                analysisToDelete = analysis
                                showDeleteConfirmation = true
                            }
                        )
                    }
                }
            }
        }
    }

    selectedAnalysis?.let { analysis ->
        AnalysisDetailsDialog(analysis) {
            selectedAnalysis = null
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Confirm Deletion", color = Color(0xFF5D4037)) },
            text = { Text("Are you sure you want to delete this analysis?", color = Color(0xFF795548)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        analysisToDelete?.let {
                            viewModel.deleteAnalysis(it["id"] as? String ?: return@let)
                        }
                        showDeleteConfirmation = false
                        analysisToDelete = null
                    }
                ) {
                    Text("Yes", color = Color(0xFFD84315))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteConfirmation = false
                    analysisToDelete = null
                }) {
                    Text("No", color = Color(0xFF795548))
                }
            },
            containerColor = Color(0xFFFFF8E1)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryItem(analysis: Map<String, Any>, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = analysis["imageUrl"] as? String,
                contentDescription = "Food Image",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .padding(end = 16.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Analysis Result",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFFD84315)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = (analysis["result"] as? String)?.take(50) + "...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF5D4037)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatTimestamp(analysis["timestamp"] as? Long ?: 0L),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF795548)
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color(0xFFD84315)
                )
            }
        }
    }
}

@Composable
fun AnalysisDetailsDialog(analysis: Map<String, Any>, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFFFF8E1),
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            ) {
                Text(
                    "Analysis Details",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFFD84315),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        AsyncImage(
                            model = analysis["imageUrl"] as? String,
                            contentDescription = "Food Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            analysis["result"] as? String ?: "",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF5D4037)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = formatTimestamp(analysis["timestamp"] as? Long ?: 0L),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF795548)
                        )
                    }
                }

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 16.dp, bottom = 8.dp)
                ) {
                    Text("Close", color = Color(0xFFD84315))
                }
            }
        }
    }
}

fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(timestamp))
}