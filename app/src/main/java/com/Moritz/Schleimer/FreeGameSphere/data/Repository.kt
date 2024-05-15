package com.Moritz.Schleimer.FreeGameSphere.data

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.Moritz.Schleimer.FreeGameSphere.data.model.Game
import com.Moritz.Schleimer.FreeGameSphere.data.remote.FreeToGameAPIService
import com.Moritz.Schleimer.FreeGameSphere.data.remote.GameApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository(
    private val api: GameApi
) {
    private val _games = MutableLiveData<List<Game>>()
    val games: LiveData<List<Game>>
        get() = _games

    suspend fun getGames() {
        try {
            val result = api.retrofitService.getAllGames()
            _games.postValue(result)
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Failed to get Games from API $e")
        }
    }
}
