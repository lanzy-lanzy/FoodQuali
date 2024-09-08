package com.food.foodquali.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.food.foodquali.data.GeminiApi
import com.food.foodquali.data.FirebaseData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class FoodQualityViewModel : ViewModel() {
    private val _analysisResult = MutableStateFlow<String?>(null)
    val analysisResult = _analysisResult.asStateFlow()

    private val _analysisHistory = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val analysisHistory = _analysisHistory.asStateFlow()

    fun analyzeFoodImage(context: Context, imageUri: Uri) {
        viewModelScope.launch {
            val bitmap = uriToBitmap(context, imageUri)
            val result = GeminiApi.analyzeImage(bitmap)
            _analysisResult.value = result
            FirebaseData.saveAnalysisResult(imageUri.toString(), result)
        }
    }

    private fun uriToBitmap(context: Context, uri: Uri): Bitmap {
        return if (Build.VERSION.SDK_INT < 28) {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        } else {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        }
    }

    fun createImageUri(context: Context): Uri {
        val directory = File(context.cacheDir, "images")
        directory.mkdirs()
        val file = File.createTempFile(
            "selected_image_",
            ".jpg",
            directory
        )
        val authority = "${context.packageName}.fileprovider"
        return FileProvider.getUriForFile(
            context,
            authority,
            file
        )
    }

    fun getFoodAnalysisHistory() {
        viewModelScope.launch {
            _analysisHistory.value = FirebaseData.getAnalysisHistory()
        }
    }
}