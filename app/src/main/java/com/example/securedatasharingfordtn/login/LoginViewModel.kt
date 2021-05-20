package com.example.securedatasharingfordtn.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.securedatasharingfordtn.database.DTNDataSharingDatabaseDao
import com.example.securedatasharingfordtn.database.LoginUserData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class LoginViewModel(
    val database: DTNDataSharingDatabaseDao,
    application: Application): AndroidViewModel(application) {

    //asyncronized job for database
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    //user information livedata
    private var _validUser = MutableLiveData<LoginUserData?>()
    val validUser: LiveData<LoginUserData?>
        get() = _validUser
    //login error snackbar indicator
    private var _loginFailSnackbarEvent = MutableLiveData<Boolean>()
    val loginFailSnackbarEvent: LiveData<Boolean>
        get() = _loginFailSnackbarEvent

    //login error snackbar indicator functions
    fun doneShowingLoginSnackbar(){
        _loginFailSnackbarEvent.value = false
    }


    //register error snackbar indicator
    private var _registerFailSnackbarEvent = MutableLiveData<Boolean>()
    val registerFailSnackbarEvent: LiveData<Boolean>
        get() = _registerFailSnackbarEvent
    //try to login
    //login error snackbar indicator functions
    fun doneShowingRegisterSnackbar(){
        _registerFailSnackbarEvent.value = false
    }





    //database query functions
    fun tryLogin(username: String, password: String) {
        var tryUser = database.tryLogin(username,password)
        if(tryUser?.userExpirationTimeMilli > System.currentTimeMillis()){
            tryUser.recentLoginTimeMilli = System.currentTimeMillis()
            database.update(tryUser)
            _validUser.value=tryUser
        }
        onTestSnackbar()
    }








    //all overloaded functions.
    //overload onclear to cancel database jobs
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    //testing functions
    fun onTestSnackbar(){
        _loginFailSnackbarEvent.value = true
    }
    fun onTestRedirect(){
        _registerFailSnackbarEvent.value = true
    }
}