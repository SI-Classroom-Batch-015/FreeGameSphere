package com.Moritz.Schleimer.FreeGameSphere

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.Moritz.Schleimer.FreeGameSphere.data.Repository
import com.Moritz.Schleimer.FreeGameSphere.data.TAG
import com.Moritz.Schleimer.FreeGameSphere.data.remote.FirebaseService
import com.Moritz.Schleimer.FreeGameSphere.data.remote.GameApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = Repository(GameApi, FirebaseService())
    private val auth = FirebaseAuth.getInstance()

    val games = repo.games
    val game = repo.game

    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?>
        get() = _user

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get()= _error

    private val _successfull = MutableLiveData<String>()
    val successfull: LiveData<String>
        get() = _successfull

    init {
        _user.value = auth.currentUser
    }

    private fun validateCredentials(email: String,passwort: String): Boolean{
        if (email.isBlank()||passwort.isBlank()){
            log("Email or Passwort is empty")
            return false
        }
        if (passwort.length < 6){
            log("Passwort to short. Must be atleast 6 Characters")
            return false
        }
        if (email.length < 5){
            log("Email needs atleast 5 Characters")
            return false
        }
        return true
    }
    fun signIn(email:String,passwort:String){
        if (!validateCredentials(email,passwort)) return

        auth.signInWithEmailAndPassword(email,passwort).addOnCompleteListener{task->
            if (task.isSuccessful){
                _user.postValue(auth.currentUser)
            }else{
                log("SignIn attempt failed")
            }
        }
    }
    fun attemptSignUp(email:String, passwort: String){
        if (!validateCredentials(email,passwort)) return
        auth.createUserWithEmailAndPassword(email,passwort).addOnCompleteListener {task->
            if (task.isSuccessful){
                _user.postValue(auth.currentUser)
            }else{
                log("SignUp attempt failed",task.exception)
            }
        }
    }

    fun logOut(){
        auth.signOut()
        _user.value= null
    }

    fun loadGames(){
        viewModelScope.launch {
            repo.getGames()
        }
    }

    fun loadGameById(id:Int){
        viewModelScope.launch {
            repo.getGameById(id)
        }
    }
    private fun log(message:String,e:Exception?=null){
        Log.e(TAG,"$message: $e")
        _error.value = message
    }
}