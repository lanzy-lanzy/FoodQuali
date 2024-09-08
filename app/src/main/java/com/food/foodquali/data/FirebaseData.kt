package com.food.foodquali.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object FirebaseData {
    private val db = FirebaseFirestore.getInstance()
    private const val COLLECTION_NAME = "food_analysis"

    suspend fun getAnalysisHistory(): List<Map<String, Any>> {
        val querySnapshot = db.collection(COLLECTION_NAME)
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .await()
        return querySnapshot.documents.map { 
            it.data?.plus("id" to it.id) ?: emptyMap()
        }
    }

    suspend fun saveAnalysisResult(imageUri: String, result: String): String {
        val analysis = hashMapOf(
            "imageUri" to imageUri,
            "result" to result,
            "timestamp" to System.currentTimeMillis()
        )
        val docRef = db.collection(COLLECTION_NAME).add(analysis).await()
        return docRef.id
    }

    suspend fun deleteAnalysisResult(id: String) {
        db.collection(COLLECTION_NAME).document(id).delete().await()
    }
}