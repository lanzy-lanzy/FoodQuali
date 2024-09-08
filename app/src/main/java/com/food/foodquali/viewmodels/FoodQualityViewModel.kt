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
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class FoodQualityViewModel : ViewModel() {
    private val _analysisResult = MutableStateFlow<String?>(null)
    val analysisResult = _analysisResult.asStateFlow()

    private val _analysisHistory = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val analysisHistory = _analysisHistory.asStateFlow()

    private val storage = FirebaseStorage.getInstance()

    fun analyzeFoodImage(context: Context, imageUri: Uri) {
        viewModelScope.launch {
            val bitmap = uriToBitmap(context, imageUri)
            val result = GeminiApi.analyzeImage(bitmap)
            _analysisResult.value = result
            saveAnalysisResult(imageUri.toString(), result)
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

    private fun getFileProviderAuthority(context: Context): String {
        return "${context.packageName}.fileprovider"
    }

    fun createImageUri(context: Context): Uri {
        val imageFile = File(context.externalCacheDir, "camera_photo.jpg")
        return FileProvider.getUriForFile(
            context,
            getFileProviderAuthority(context),
            imageFile
        )
    }

    fun getFoodAnalysisHistory() {
        viewModelScope.launch {
            _analysisHistory.value = FirebaseData.getAnalysisHistory()
        }
    }

    private fun saveAnalysisResult(imageUrl: String, result: String) {
        viewModelScope.launch {
            FirebaseData.saveAnalysisResult(imageUrl, result)
            getFoodAnalysisHistory()
        }
    }

    fun deleteAnalysis(id: String) {
        viewModelScope.launch {
            FirebaseData.deleteAnalysisResult(id)
            getFoodAnalysisHistory() // Refresh the history after deletion
        }
    }

    fun clearAnalysisResult() {
        _analysisResult.value = null
    }

    fun uploadImageToFirebase(uri: Uri, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val timestamp = System.currentTimeMillis()
        val imageName = "food_image_$timestamp.jpg"
        val imageRef = storage.reference.child("images/$imageName")

        imageRef.putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    onSuccess(downloadUri.toString())
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}
