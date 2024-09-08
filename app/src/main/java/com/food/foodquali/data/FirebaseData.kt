package com.food.foodquali.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object FirebaseData {
    private val db = FirebaseFirestore.getInstance()
    private val analysisCollection = db.collection("food_analysis")

    suspend fun saveAnalysisResult(imageUri: String, result: String) {
        val analysis = hashMapOf(
            "imageUri" to imageUri,
            "result" to result,
            "timestamp" to System.currentTimeMillis()
        )
        analysisCollection.add(analysis).await()
    }

    suspend fun getAnalysisHistory(): List<Map<String, Any>> {
        val querySnapshot = analysisCollection
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .await()
        return querySnapshot.documents.mapNotNull { it.data }
    }
}

suspend fun deleteAnalysisResult(id: String) {
    analysisCollection.document(id).delete().await()
}
