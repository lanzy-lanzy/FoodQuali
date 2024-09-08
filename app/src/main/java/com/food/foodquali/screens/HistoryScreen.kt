package com.food.foodquali.screens
  import androidx.compose.foundation.clickable
  import androidx.compose.foundation.layout.*
  import androidx.compose.foundation.lazy.LazyColumn
  import androidx.compose.foundation.lazy.items
  import androidx.compose.material.icons.Icons
  import androidx.compose.material.icons.filled.ArrowBack
  import androidx.compose.material.icons.filled.Delete
  import androidx.compose.material3.*
  import androidx.compose.runtime.*
  import androidx.compose.ui.Alignment
  import androidx.compose.ui.Modifier
  import androidx.compose.ui.layout.ContentScale
  import androidx.compose.ui.unit.dp
  import androidx.navigation.NavController
  import androidx.lifecycle.viewmodel.compose.viewModel
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

        LaunchedEffect(key1 = Unit) {
            viewModel.getFoodAnalysisHistory()
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Analysis History") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { innerPadding ->
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

        selectedAnalysis?.let { analysis ->
            AnalysisDetailsDialog(analysis) {
                selectedAnalysis = null
            }
        }

        if (showDeleteConfirmation) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmation = false },
                title = { Text("Confirm Deletion") },
                text = { Text("Are you sure you want to delete this analysis?") },
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
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { 
                        showDeleteConfirmation = false
                        analysisToDelete = null
                    }) {
                        Text("No")
                    }
                }
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
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                        .padding(end = 16.dp),
                    contentScale = ContentScale.Crop
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Analysis Result",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = (analysis["result"] as String).take(50) + "...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatTimestamp(analysis["timestamp"] as Long),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }

@Composable
fun AnalysisDetailsDialog(analysis: Map<String, Any>, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Analysis Details") },
        text = {
            Column {
                AsyncImage(
                    model = analysis["imageUrl"] as? String,
                    contentDescription = "Food Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(analysis["result"] as String)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = formatTimestamp(analysis["timestamp"] as Long),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
