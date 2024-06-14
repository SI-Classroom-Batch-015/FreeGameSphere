package com.Moritz.Schleimer.FreeGameSphere.data.remote

import com.Moritz.Schleimer.FreeGameSphere.data.model.Game
import com.Moritz.Schleimer.FreeGameSphere.data.model.Profile
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirestoreService(
    private val uid: String
) {
    private val database = Firebase.firestore

    suspend fun setUser(profile: Profile){
        database.collection("Profiles").document(uid).set(profile).await()
    }

    suspend fun getProfile(uid: String): Profile? {
        val result = database.collection("Profiles").document(uid).get().await()
        return result.toObject(Profile::class.java)
    }

    suspend fun addToFavorites(uid: String,game: Game){
        database.collection("Profiles").document(uid).collection("Favorites").document(game.id.toString()).set(
            mapOf("likedAt" to Timestamp.now())
        ) .await()
    }

    suspend fun removeFromFavorites(uid: String,game: Game){
        database.collection("Profiles").document(uid).collection("Favorites").document(game.id.toString()).delete().await()
    }

    suspend fun getFavorites(uid: String):List<Int>{
        val result = database.collection("Profiles").document(uid).collection("Favorites").get().await()
        return result.documents.map { it.id.toInt() }
    }
}