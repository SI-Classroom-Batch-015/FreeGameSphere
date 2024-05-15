package com.Moritz.Schleimer.FreeGameSphere

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.Moritz.Schleimer.FreeGameSphere.data.Repository
import com.Moritz.Schleimer.FreeGameSphere.data.remote.GameApi
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = Repository(GameApi)

    val games = repo.games


    fun loadGames(){
        viewModelScope.launch {
            repo.getGames()
        }
    }
}