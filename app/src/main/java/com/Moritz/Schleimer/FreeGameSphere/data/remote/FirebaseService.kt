package com.Moritz.Schleimer.FreeGameSphere.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirebaseService {


    private val firebaseAuth = FirebaseAuth.getInstance()
    private var user: FirebaseUser? = firebaseAuth.currentUser

    fun getCurrentUser():FirebaseUser?{
        return user
    }

    val isLoggedIn: Boolean
        get() = user != null

    val userId: String?
        get() = user?.uid

    val email: String?
        get() = user?.email

    suspend fun signInWithEmailAndPassword(email: String, password: String): Boolean {
        val result = Firebase.auth.signInWithEmailAndPassword(email, password).await()
        user = result.user
        return user != null
    }

    suspend fun createUserWithEmailAndPassword(email: String, password: String): Boolean {
        val result = Firebase.auth.createUserWithEmailAndPassword(email, password).await()
        user = result.user
        return user != null
    }
    fun signOut(){
        Firebase.auth.signOut()
        user = null
    }
}