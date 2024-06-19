package com.Moritz.Schleimer.FreeGameSphere.data

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.Moritz.Schleimer.FreeGameSphere.data.model.Game
import com.Moritz.Schleimer.FreeGameSphere.data.model.Profile
import com.Moritz.Schleimer.FreeGameSphere.data.remote.FirebaseService
import com.Moritz.Schleimer.FreeGameSphere.data.remote.FirestoreService
import com.Moritz.Schleimer.FreeGameSphere.data.remote.GameApi
import com.google.firebase.auth.FirebaseUser
import java.io.File

const val TAG = "Repository"

class Repository(
    private val api: GameApi,
    private val firebaseService: FirebaseService

) {


    private val _firebaseUser = MutableLiveData<FirebaseUser?>()
    val firebaseUser: LiveData<FirebaseUser?>
        get() = _firebaseUser

    private val _userProfile = MutableLiveData<Profile?>()
    val userProfile: LiveData<Profile?>
        get() = _userProfile

    private val _games = MutableLiveData<List<Game>>()
    private val _favoriteGameIds = MutableLiveData<List<Int>>()
    private val _gameDescriptions = MutableLiveData<Map<Int, String>>(emptyMap())


    private val _gamesWithFavorites = MediatorLiveData<List<Game>>().apply {
        addSource(_games) {
            value = combineGamesWithFavoriteIds(
                it,
                _gameDescriptions.value ?: emptyMap(),
                _favoriteGameIds.value?.toSet() ?: emptySet()
            )
        }
        addSource(_favoriteGameIds) {
            value = combineGamesWithFavoriteIds(
                _games.value ?: emptyList(),
                _gameDescriptions.value ?: emptyMap(),
                it.toSet()
            )
        }
        addSource(_gameDescriptions) {
            value = combineGamesWithFavoriteIds(
                _games.value ?: emptyList(),
                it,
                _favoriteGameIds.value?.toSet() ?: emptySet()
            )
        }
    }

    private fun combineGamesWithFavoriteIds(
        games: List<Game>,
        longDescriptions: Map<Int, String>,
        favoriteGameIds: Set<Int>
    ): List<Game> {
        return games.map {
            it.copy(
                description = longDescriptions[it.id],
                isLiked = it.id in favoriteGameIds
            )
        }
    }

    val games: LiveData<List<Game>>
        get() = _gamesWithFavorites

    suspend fun loadGames() {
        try {
            val result = api.retrofitService.getAllGames()
            _games.postValue(result)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get Games from API $e")
        }
    }

    fun getCurrentUser() {
        _firebaseUser.value = firebaseService.getCurrentUser()
    }

    suspend fun loadLongDescriptionByGameId(id: Int) {
        try {
            val newDescription = api.retrofitService.getGameById(id).description
            if (newDescription != null) {
                val descriptions = _gameDescriptions.value?.toMutableMap()
                descriptions?.put(id, newDescription)
                _gameDescriptions.value = descriptions?.toMap()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get Game by ID from API $e")
        }
    }

    suspend fun signInUser(email: String, password: String): Boolean {
        return try {
            firebaseService.signInWithEmailAndPassword(email, password)
            getFavoriteGames()
            getCurrentUser()
            getUserProfile()
            true
        } catch (e: Exception) {
            Log.d(TAG, e.message.toString())
            false
        }
    }

    suspend fun signUpUser(email: String, password: String): Boolean {
        return try {
            firebaseService.createUserWithEmailAndPassword(email, password)
            val profile = Profile()
            setProfile(profile)
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            false
        }
    }

    fun signOut() {
        firebaseService.signOut()
        _userProfile.value = null
        _favoriteGameIds.value = emptyList()
    }

    private suspend fun setProfile(profile: Profile): Boolean {
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

     suspend fun getUserProfile() {
        try {
            val uid = firebaseService.userId ?: return
            val firestoreService = FirestoreService(uid)
            _userProfile.value = firestoreService.getProfile(uid)
        } catch (e: Exception) {
            Log.e(Repository::class.simpleName, "Could not get profile")
        }
    }

    suspend fun addGameToFavorite(game: Game) {
        try {
            val uid =
                firebaseService.userId ?: throw Exception("Could not get UID in addGameToFavorite")
            val firestoreService = FirestoreService(uid)
            firestoreService.addToFavorites(uid, game)
            getFavoriteGames()
        } catch (e: Exception) {
            Log.e(Repository::class.simpleName, "Could not add Favorite to Firebase: $e")
        }
    }

    suspend fun getFavoriteGames() {
        try {
            val uid =
                firebaseService.userId ?: throw Exception("Could not get UID in getFavoriteGames")
            val firestoreService = FirestoreService(uid)
            val result = firestoreService.getFavorites(uid)
            _favoriteGameIds.value = result
        } catch (e: Exception) {
            Log.e(Repository::class.simpleName, "Could not get favorite Games from Firebase: $e")
        }
    }

    suspend fun removeFavoriteGame(game: Game) {
        try {
            val uid = firebaseService.userId ?: return
            val firestoreService = FirestoreService(uid)
            firestoreService.removeFromFavorites(uid, game)
            getFavoriteGames()
        } catch (e: Exception) {
            Log.e(Repository::class.simpleName, "Could not remove Favorite from Firebase: $e")
        }
    }
    // STORAGE
    suspend fun uploadProfilePhoto(file: File): Uri? {
        return try {
            val uid = firebaseService.userId ?: ""
            val firestoreService = FirestoreService(uid)
            firestoreService.uploadPhoto(file)
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading profile photo: ${e.message}")
            null
        }
    }
    suspend fun updateProfile(profile: Profile){
        try {
            val uid = firebaseService.userId ?: return
            val firestoreService = FirestoreService(uid)
            firestoreService.updateProfile(profile)
        }catch (e: Exception) {
            Log.e(TAG, "Error update profile: ${e.message}")
        }
    }
}

