package com.Moritz.Schleimer.FreeGameSphere

import android.app.Application
import android.net.Uri
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.Moritz.Schleimer.FreeGameSphere.data.Repository
import com.Moritz.Schleimer.FreeGameSphere.data.model.Game
import com.Moritz.Schleimer.FreeGameSphere.data.remote.FirebaseService
import com.Moritz.Schleimer.FreeGameSphere.data.remote.GameApi
import com.google.firebase.auth.EmailAuthProvider
import kotlinx.coroutines.launch
import java.io.File
import java.lang.Exception

enum class STATES{DEFAULT,LOADING,SUCCESS,ERROR}
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = Repository(GameApi, FirebaseService())

    val games = repo.games
    val currentUser = repo.firebaseUser
    val currentProfile = repo.userProfile
    val favoriteGames = repo.games.map { games ->
        games.filter { it.isLiked }
    }

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?>
        get()= _error

    private val _currentState = MutableLiveData(STATES.DEFAULT)
    val currentState: LiveData<STATES> get() = _currentState


    private val _selectedGameId = MutableLiveData<Int?>()
    val selectedGame: LiveData<Game?> = _selectedGameId.switchMap { id ->
        repo.games.map { it.find { it.id == id } }
    }

    val isFavorite = selectedGame.map { it?.isLiked ?: false }



    init {
        repo.getCurrentUser()
        viewModelScope.launch{
            repo.getFavoriteGames()
        }
    }

    fun loadUserProfile(){
        viewModelScope.launch {
            repo.getUserProfile()
        }
    }
    fun toggleFavorite(){
        val game = selectedGame.value ?: throw Exception("Game is null in toggleFavorites")
        viewModelScope.launch {
            if (isFavorite.value == true) {
                repo.removeFavoriteGame(game)
            } else {
                repo.addGameToFavorite(game)
            }
        }
    }

    fun signUp(email:String,password:String,password2: String){
        if (!validateCredentialsSignUp(email,password,password2)) return
        viewModelScope.launch{
            _currentState.value = STATES.LOADING
            val isSuccess = repo.signUpUser(email, password)
            if (isSuccess){
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
                _currentState.value = STATES.SUCCESS
            }else{
                _currentState.value = STATES.ERROR
                _error.value = "Email and password are incorrect"
            }
        }
    }

    fun signOut(){
        repo.signOut()
        repo.getCurrentUser()
    }


    fun loadGames() {
        viewModelScope.launch {
            repo.loadGames()
        }
    }

    fun selectGameById(id: Int) {
        _selectedGameId.value = id
        viewModelScope.launch {
            repo.loadLongDescriptionByGameId(id)
        }
    }
    fun clearError(){
        _error.value = null
    }
    fun clearState(){
        _currentState.value = null
    }
    //Storage
    private val _profilePhotoUrl = MutableLiveData<Uri?>()
    val profilePhotoUrl: LiveData<Uri?>
        get() = _profilePhotoUrl

    fun uploadProfilePhoto(file:File){
        viewModelScope.launch {
            val dowloadUrl = repo.uploadProfilePhoto(file)
            _profilePhotoUrl.value = dowloadUrl
        }
    }
    fun updateProfile(image:String){
        viewModelScope.launch {
            val currentProfile = currentProfile.value
            val updateProfile = currentProfile?.copy(imageUrl = image)
            if (updateProfile != null) {
                repo.updateProfile(updateProfile)
            }
        }
    }

    fun changePassword(currentPassword: String, newPassword: String) {
        val user = currentUser.value
        val credential = EmailAuthProvider.getCredential(user!!.email!!, currentPassword)

        user.reauthenticate(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                user.updatePassword(newPassword)

            }
        }
    }
}
