package com.Moritz.Schleimer.FreeGameSphere.data

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.Moritz.Schleimer.FreeGameSphere.data.model.Game
import com.Moritz.Schleimer.FreeGameSphere.data.model.Profile
import com.Moritz.Schleimer.FreeGameSphere.data.remote.FirebaseService
import com.Moritz.Schleimer.FreeGameSphere.data.remote.FirestoreService
import com.Moritz.Schleimer.FreeGameSphere.data.remote.FreeToGameAPIService
import com.Moritz.Schleimer.FreeGameSphere.data.remote.GameApi
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
const val TAG = "Repository"
class Repository(
    private val api: GameApi,
    private val firebaseService: FirebaseService

) {

    val isLoggedIn = firebaseService.isLoggedIn

    private val _currentUser = MutableLiveData<FirebaseUser?>()
    val currentUser:LiveData<FirebaseUser?>
        get() = _currentUser

    private val _games = MutableLiveData<List<Game>>()
    val games: LiveData<List<Game>>
        get() = _games

    private val _game = MutableLiveData<Game>()
    val game: LiveData<Game>
        get() = _game

    private val _userProfile = MutableLiveData<Profile>()
    val userProfile: LiveData<Profile>
        get() = _userProfile

    private val _favoriteGames = MutableLiveData<List<Game>>()
    val favoriteGames: LiveData<List<Game>>
        get() = _favoriteGames

    suspend fun getGames() {
        try {
            val result = api.retrofitService.getAllGames()
            _games.postValue(result)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get Games from API $e")
        }
    }

    fun getCurrentUser(){
        _currentUser.value = firebaseService.getCurrentUser()
    }

    suspend fun getGameById(id: Int) {
        try {
            val result = api.retrofitService.getGameById(id)
            _game.postValue(result)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get Game by ID from API $e")
        }
    }

    suspend fun signInUser(email: String, password: String): Boolean {
        return try {
            firebaseService.signInWithEmailAndPassword(email, password)
        } catch (e: Exception) {
            Log.d(TAG, e.message.toString())
            false
        }
    }

    suspend fun signUpUser(email: String, password: String): Boolean {
        return try {
            firebaseService.createUserWithEmailAndPassword(email, password)
        } catch (e: Exception) {
            Log.e(TAG,e.message.toString())
            false
        }
    }

    fun signOut() {
        firebaseService.signOut()
    }

    suspend fun setProfile(profile: Profile): Boolean {
        try {
            val uid = firebaseService.userId ?: return false
            val firestoreService = FirestoreService(uid)
            firestoreService.setUser(profile)
            getUserProfile()
            return true
        } catch (e: Exception) {
            Log.e(Repository::class.simpleName, "Could not create a profile")
            return false
        }
    }
    private suspend fun getUserProfile() {
        try {
            val uid = firebaseService.userId ?: return
            val firestoreService = FirestoreService(uid)
            _userProfile.value = firestoreService.getProfile(uid)
        } catch (e: Exception) {
            Log.e(Repository::class.simpleName, "Could not get profile")
        }
    }

    suspend fun addGameToFavorite(game:Game){
        try {
            val uid = firebaseService.userId ?: return
            val firestoreService = FirestoreService(uid)
            firestoreService.addToFavorites(uid,game)
        }catch (e:Exception){
            Log.e(Repository::class.simpleName,"Could not add Favorite to Database")
        }
    }

    suspend fun getFavoriteGame() {
        try {
            val uid = firebaseService.userId ?: return
            val firestoreService = FirestoreService(uid)
            val result = firestoreService.getFavorites(uid)
            _favoriteGames.value = result
        } catch (e: Exception) {
            Log.e(Repository::class.simpleName, e.message.toString())
        }
    }

    suspend fun removeFavoriteGame(game: Game){
        try {
            val uid = firebaseService.userId ?: return
            val firestoreService = FirestoreService(uid)
            firestoreService.removeFromFavorites(uid,game)
        }catch (e:Exception){
            Log.e(Repository::class.simpleName,"Could not remove Favorite from Database")
        }
    }
}

