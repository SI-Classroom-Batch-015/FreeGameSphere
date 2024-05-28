package com.Moritz.Schleimer.FreeGameSphere.data

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.Moritz.Schleimer.FreeGameSphere.data.model.Game
import com.Moritz.Schleimer.FreeGameSphere.data.remote.FirebaseService
import com.Moritz.Schleimer.FreeGameSphere.data.remote.FreeToGameAPIService
import com.Moritz.Schleimer.FreeGameSphere.data.remote.GameApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
const val TAG = "Repository"
class Repository(
    private val api: GameApi,
    private val firebaseService: FirebaseService

) {
    private val _games = MutableLiveData<List<Game>>()
    val games: LiveData<List<Game>>
        get() = _games

    private val _game = MutableLiveData<Game>()
    val game: LiveData<Game>
        get() = _game

    suspend fun getGames() {
        try {
            val result = api.retrofitService.getAllGames()
            _games.postValue(result)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get Games from API $e")
        }
    }
    suspend fun getGameById(id:Int){
        try {
            val result = api.retrofitService.getGameById(id)
            _game.postValue(result)
        }catch (e:Exception){
            Log.e(TAG,"Failed to get Game by ID from API $e")
        }
    }
    suspend fun signInUser(email: String, password: String): Boolean {
        return firebaseService.signInWithEmailAndPassword(email, password)
    }
}
