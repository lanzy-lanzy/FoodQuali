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
            text("""
                Analyze this food image and provide the following details:
                1. Quality assessment
                2. Freshness evaluation
                3. Potential issues or concerns
                4. Suggestions for improvement
                5. Recommendations for storage or consumption

                Please format the response in clear, separate sections.
            """.trimIndent())
        }

        val response = generativeModel.generateContent(inputContent)
        response.text ?: "Unable to analyze the image."
    }
}