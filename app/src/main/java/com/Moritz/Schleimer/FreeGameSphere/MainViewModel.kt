package com.Moritz.Schleimer.FreeGameSphere

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.Moritz.Schleimer.FreeGameSphere.data.Repository
import com.Moritz.Schleimer.FreeGameSphere.data.model.Game
import com.Moritz.Schleimer.FreeGameSphere.data.model.Profile
import com.Moritz.Schleimer.FreeGameSphere.data.remote.FirebaseService
import com.Moritz.Schleimer.FreeGameSphere.data.remote.GameApi
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
enum class STATES{LOADING,SUCCESS,ERROR}
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = Repository(GameApi, FirebaseService())
    private val auth = FirebaseAuth.getInstance()

    val games = repo.games
    val game = repo.game
    val currentUser = repo.currentUser
    val favoriteGames = repo.favoriteGames

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?>
        get()= _error

    private val _currentState = MutableLiveData<STATES?>()
    val currentState : LiveData<STATES?> get() = _currentState

    init {
        setupUser()
    }

    private fun setupUser(){
        repo.getCurrentUser()
    }

    fun signUp(email:String,password:String,password2: String){
        if (!validateCredentialsSignUp(email,password,password2)) return
        viewModelScope.launch{
            _currentState.value = STATES.LOADING
            val isSuccess = repo.signUpUser(email, password)
            if (isSuccess){
                val profile = Profile()
                repo.setProfile(profile)
                _currentState.value = STATES.SUCCESS
            }else{
                _currentState.value = STATES.ERROR
                _error.value = "User already exists"
            }
        }
    }
    private fun validateCredentialsSignIn(email: String, passwort: String): Boolean{
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            _error.value = "Invalid Email Adress"
            return false
        }
        if (email.isBlank()||passwort.isBlank()){
            _error.value = "Email or Passwort is empty"
            return false
        }
        if (passwort.length < 6){
            _error.value = "Passwort to short. Must be atleast 6 Characters"
            return false
        }
        if (email.length < 5){
            _error.value = "Email needs atleast 5 Characters"
            return false
        }
        return true
    }

    private fun validateCredentialsSignUp(email: String,password: String,password2: String):Boolean{
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            _error.value = "Invalid Email Adress"
            return false
        }
        if (email.isBlank()||password.isBlank()||password2.isBlank()){
            _error.value = "Email or Passwort is empty"
            return false
        }
        if (password.length < 6){
            _error.value = "Password to short. Must be atleast 6 Characters"
            return false
        }
        if (email.length < 5){
            _error.value = "Email needs atleast 5 Characters"
            return false
        }
        if (password != password2){
            _error.value = "Passwords do not match"
            return false
        }
        return true
    }

    fun signIn(email: String,password: String){
        if (!validateCredentialsSignIn(email,password)) return
        _currentState.value = STATES.LOADING
        viewModelScope.launch {
            val isSuccess = repo.signInUser(email, password)
            if (isSuccess){
                setupUser()
                _currentState.value = STATES.SUCCESS
            }else{
                _currentState.value = STATES.ERROR
                _error.value = "Email and password are incorrect"
            }
        }
    }

    fun signOut(){
        repo.signOut()
        setupUser()
    }


    fun loadGames() {
        viewModelScope.launch {
            repo.getGames()
        }
    }

    fun loadGameById(id: Int) {
        viewModelScope.launch {
            repo.getGameById(id)
        }
    }
    fun clearError(){
        _error.value = null
    }
    fun clearState(){
        _currentState.value = null
    }
    fun addToFavorite(game: Game){
        viewModelScope.launch {
            repo.addGameToFavorite(game)
        }
    }

    fun removeFavorite(game: Game){
        viewModelScope.launch {
            repo.removeFavoriteGame(game)
        }
    }
    fun loadFavorites(){
        viewModelScope.launch{
            repo.getFavoriteGame()
        }
    }

}
