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
            text(
                """
                Analyze this food image and provide the following details:
                **1. Quality assessment**
                **2. Freshness evaluation**
                **3. Potential issues or concerns**
                **4. Suggestions for improvement**
                **5. Recommendations for storage or consumption**

                Please format the response in clear, separate sections without using asterisks or bullet points. Use formal language and complete sentences.
            """.trimIndent()
            )
        }

        val response = generativeModel.generateContent(inputContent)
        response.text?.let { formatResponse(it) } ?: "Unable to analyze the image."
    }
    private fun formatResponse(text: String): String {
        val sections = listOf(
            "Quality assessment",
            "Freshness evaluation",
            "Potential issues or concerns",
            "Suggestions for improvement",
            "Recommendations for storage or consumption"
        )
    
        var formattedText = text
        sections.forEachIndexed { index, section ->
            formattedText = formattedText.replace(
                "**${index + 1}. $section**",
                "## **${index + 1}. $section**\n"
            )
        }
        return formattedText.replace(Regex("[*•#]"), "").trim()
    }
}