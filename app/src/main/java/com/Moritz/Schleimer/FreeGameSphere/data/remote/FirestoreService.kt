package com.Moritz.Schleimer.FreeGameSphere.data.remote

import android.net.Uri
import androidx.core.net.toUri
import com.Moritz.Schleimer.FreeGameSphere.data.model.Game
import com.Moritz.Schleimer.FreeGameSphere.data.model.Profile
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import kotlinx.coroutines.tasks.await
import java.io.File

class FirestoreService(
    private val uid: String
) {
    private val database = Firebase.firestore

    suspend fun setUser(profile: Profile){
        database.collection("Profiles").document(uid).set(profile).await()
    }

    suspend fun updateProfile(profile: Profile) {
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
    //Storage
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference

    suspend fun uploadPhoto(file: File): Uri {
        val photoRef = storageRef.child("photos/$uid/${file.name}")
        val fileUri = file.toUri()

        val metadata = StorageMetadata.Builder()
            .setContentType("image/jpeg")
            .build()

        photoRef.putFile(fileUri, metadata).await()
        return photoRef.downloadUrl.await()
    }
}