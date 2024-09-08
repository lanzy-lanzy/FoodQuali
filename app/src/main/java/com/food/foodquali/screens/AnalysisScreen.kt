package com.food.foodquali.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.ArrowBack
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.food.foodquali.viewmodels.FoodQualityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisScreen(navController: NavController) {
    val viewModel: FoodQualityViewModel = viewModel()
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val analysisResult by viewModel.analysisResult.collectAsState()

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            // The image has been saved to the Uri
            imageUri?.let { uri ->
                viewModel.analyzeFoodImage(context, uri)
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
            viewModel.analyzeFoodImage(context, it)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Food Analysis") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
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
            Button(
                onClick = {
                    val uri = viewModel.createImageUri(context)
                    imageUri = uri
                    cameraLauncher.launch(uri)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Camera, contentDescription = "Capture")
                Spacer(Modifier.width(8.dp))
                Text("Capture Image")
            }

            Button(
                onClick = { galleryLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Upload, contentDescription = "Upload")
                Spacer(Modifier.width(8.dp))
                Text("Upload Image")
            }

            imageUri?.let { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = "Selected Image",
                    modifier = Modifier
                        .size(200.dp)
                        .padding(16.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Button(
                onClick = { 
                    imageUri?.let { uri ->
                        viewModel.analyzeFoodImage(context, uri)
                    }
                },
                enabled = imageUri != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Analyze Food")
            }

            analysisResult?.let {
                Text("Analysis Result: $it")
            }
        }
    }
}