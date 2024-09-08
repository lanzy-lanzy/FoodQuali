package com.food.foodquali.data

import android.graphics.Bitmap
import android.net.Uri
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object GeminiApi {
    private const val API_KEY = "AIzaSyBD15s-m0ClELhAR7XbbVPRkSFlQzcu_fQ"
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = API_KEY
    )

    suspend fun analyzeImage(bitmap: Bitmap): String = withContext(Dispatchers.Default) {
        val inputContent = content {
            image(bitmap)
            text("Analyze this food image and provide details about its quality, freshness, and potential issues.")
        }

        val response = generativeModel.generateContent(inputContent)
        response.text ?: "Unable to analyze the image."
    }
}