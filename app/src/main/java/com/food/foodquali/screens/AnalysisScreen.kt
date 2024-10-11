package com.food.foodquali.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.food.foodquali.viewmodels.FoodQualityViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@RequiresApi(Build.VERSION_CODES.P)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun AnalysisScreen(navController: NavController) {
    val viewModel: FoodQualityViewModel = viewModel()
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val analysisResult by viewModel.analysisResult.collectAsState()
    var showCameraPreview by remember { mutableStateOf(false) }
    var isAnalyzing by remember { mutableStateOf(false) }
    var showHowItWorks by remember { mutableStateOf(true) }

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFFF3E0),
            Color(0xFFFFE0B2)
        )
    )

    val refreshState = rememberPullRefreshState(
        refreshing = false,
        onRefresh = {
            imageUri = null
            viewModel.clearAnalysisResult()
            isAnalyzing = false
            showHowItWorks = true
        }
    )

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            showCameraPreview = true
        } else {
            Log.e("AnalysisScreen", "Camera permission denied.")
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
            isAnalyzing = true
            showHowItWorks = false
            viewModel.uploadImageToFirebase(it,
                onSuccess = { downloadUrl ->
                    viewModel.analyzeFoodImage(context, Uri.parse(downloadUrl))
                },
                onFailure = { exception ->
                    Log.e("AnalysisScreen", "Failed to upload image", exception)
                    isAnalyzing = false
                }
            )
        }
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        "Food Analysis",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5D4037)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF5D4037)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("history") }) {
                        Icon(
                            Icons.Default.History,
                            contentDescription = "History",
                            tint = Color(0xFF5D4037)
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
                .pullRefresh(refreshState)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                AnimatedVisibility(
                    visible = !showCameraPreview,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    AnalysisContent(
                        imageUri = imageUri,
                        isAnalyzing = isAnalyzing,
                        analysisResult = analysisResult,
                        showHowItWorks = showHowItWorks,
                        onCaptureClick = {
                            val permission = Manifest.permission.CAMERA
                            when {
                                ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED -> {
                                    showCameraPreview = true
                                }
                                else -> cameraPermissionLauncher.launch(permission)
                            }
                        },
                        onUploadClick = { galleryLauncher.launch("image/*") },
                        onAnalyzeAnother = {
                            imageUri = null
                            viewModel.clearAnalysisResult()
                            isAnalyzing = false
                            showHowItWorks = true
                        },
                        onToggleHowItWorks = { showHowItWorks = !showHowItWorks }
                    )
                }

                if (showCameraPreview) {
                    CameraPreview(
                        onImageCaptured = { uri ->
                            imageUri = uri
                            showCameraPreview = false
                            isAnalyzing = true
                            showHowItWorks = false
                            viewModel.analyzeFoodImage(context, uri)
                        },
                        onError = { Log.e("Camera", "View error:", it) }
                    )
                }
            }
            PullRefreshIndicator(refreshing = false, state = refreshState, Modifier.align(Alignment.TopCenter))
        }
    }
}

@Composable
fun AnalysisContent(
    imageUri: Uri?,
    isAnalyzing: Boolean,
    analysisResult: String?,
    showHowItWorks: Boolean,
    onCaptureClick: () -> Unit,
    onUploadClick: () -> Unit,
    onAnalyzeAnother: () -> Unit,
    onToggleHowItWorks: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ActionButtons(onCaptureClick, onUploadClick)

        ImagePreview(imageUri)

        AnalysisStatus(isAnalyzing, analysisResult)

        if (analysisResult != null) {
            Button(
                onClick = onAnalyzeAnother,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Analyze Another")
                Spacer(Modifier.width(8.dp))
                Text("Analyze Another Image")
            }
        }

        AnimatedVisibility(
            visible = showHowItWorks,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            HowItWorks(onToggleHowItWorks)
        }
    }
}


@Composable
fun ActionButtons(onCaptureClick: () -> Unit, onUploadClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ActionButton(
            onClick = onCaptureClick,
            icon = Icons.Default.Camera,
            label = "Capture",
            color = Color(0xFFD84315)
        )
        ActionButton(
            onClick = onUploadClick,
            icon = Icons.Default.Upload,
            label = "Upload",
            color = Color(0xFF00897B)
        )
    }
}

@Composable
fun ActionButton(onClick: () -> Unit, icon: ImageVector, label: String, color: Color) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(150.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = label, modifier = Modifier.size(48.dp))
            Spacer(Modifier.height(8.dp))
            Text(label, fontSize = 16.sp)
        }
    }
}

@Composable
fun ImagePreview(imageUri: Uri?) {
    imageUri?.let { uri ->
        Card(
            modifier = Modifier
                .size(300.dp)
                .clip(RoundedCornerShape(16.dp)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            AsyncImage(
                model = uri,
                contentDescription = "Selected Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}


@Composable
fun AnalysisStatus(isAnalyzing: Boolean, analysisResult: String?) {
    when {
        isAnalyzing && analysisResult == null -> {
            CircularProgressIndicator(
                modifier = Modifier.size(50.dp),
                color = Color(0xFFD84315)
            )
            Text(
                "Analyzing food...",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF5D4037)
            )
        }
        analysisResult != null -> {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Analysis Result",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFFD84315),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val sections = analysisResult.split("\n\n")
                    sections.forEach { section ->
                        val lines = section.split("\n")
                        lines.forEachIndexed { index, line ->
                            if (index == 0 && line.matches(Regex("\\d+\\..+"))) {
                                Text(
                                    text = line,
                                    style = MaterialTheme.typography.titleMedium,  // Bigger title
                                    color = Color(0xFF1E1D1D),
                                    fontWeight = FontWeight.Bold
                                )
                            } else if (line.contains("Quality assessment") || line.contains("Freshness evaluation") ||
                                line.contains("Potential issues or concerns") || line.contains("Suggestions for improvement") ||
                                line.contains("Recommendations for storage or consumption")) {
                                Text(
                                    text = line,
                                    style = MaterialTheme.typography.titleMedium,  // Bigger title
                                    color = Color(0xFF151414),
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Text(
                                    text = line,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF5D4037)
                                )
                            }
                            if (index < lines.size - 1) {
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}
@Composable
fun HowItWorks(onToggleHowItWorks: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2F1))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "How It Works",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF00796B),
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onToggleHowItWorks) {
                    Icon(
                        Icons.Default.KeyboardArrowUp,
                        contentDescription = "Collapse",
                        tint = Color(0xFF00796B)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            HowItWorksStep(
                icon = Icons.Default.Camera,
                title = "Capture or Upload",
                description = "Take a photo or select an image of your food"
            )
            HowItWorksStep(
                icon = Icons.Default.CloudUpload,
                title = "Analysis",
                description = "Our AI processes the image to identify the food"
            )
            HowItWorksStep(
                icon = Icons.Default.Assessment,
                title = "Results",
                description = "Get detailed information about your food's quality and nutrition"
            )
        }
    }
}
@Composable
fun HowItWorksStep(icon: ImageVector, title: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color(0xFF00796B), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = title, tint = Color.White)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF00796B)
            )
            Text(
                description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF004D40)
            )
        }
    }
}
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun CameraPreview(
    onImageCaptured: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var previewUseCase by remember { mutableStateOf<Preview?>(null) }
    val imageCaptureUseCase = ImageCapture.Builder()
        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
        .build()

    var previewView by remember { mutableStateOf<PreviewView?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                PreviewView(ctx).apply {
                    this.scaleType = PreviewView.ScaleType.FILL_CENTER
                    previewView = this
                }
            }
        )

        LaunchedEffect(previewView) {
            val cameraProvider = context.getCameraProvider()
            previewUseCase = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView?.surfaceProvider)
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    previewUseCase,
                    imageCaptureUseCase
                )
            } catch (e: Exception) {
                Log.e("CameraPreview", "Use case binding failed", e)
            }
        }

        Button(
            onClick = {
                val file = File(
                    context.externalMediaDirs.firstOrNull(),
                    SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
                        .format(System.currentTimeMillis()) + ".jpg"
                )
                val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()
                imageCaptureUseCase.takePicture(
                    outputOptions,
                    context.mainExecutor,
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                            val savedUri = Uri.fromFile(file)
                            onImageCaptured(savedUri)
                        }

                        override fun onError(exc: ImageCaptureException) {
                            onError(exc)
                        }
                    }
                )
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Text("Capture")
        }
    }
}

suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { cameraProviderFuture ->
        cameraProviderFuture.addListener({
            continuation.resume(cameraProviderFuture.get())
        }, ContextCompat.getMainExecutor(this))
    }
}

