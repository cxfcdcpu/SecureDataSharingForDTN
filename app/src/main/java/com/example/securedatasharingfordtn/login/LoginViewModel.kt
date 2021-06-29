package com.example.securedatasharingfordtn.login

import android.app.Application
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.securedatasharingfordtn.database.DTNDataSharingDatabaseDao
import com.example.securedatasharingfordtn.database.LoginUserData
import kotlinx.coroutines.*

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


    //properties
    val username = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val missionCode = MutableLiveData<String>()
    val lastLoginTime = MutableLiveData<Long>()
    var tabSelect = MutableLiveData<Boolean>()



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
    //Camera event
    private var _useCameraEvent = MutableLiveData<Boolean>()
    val useCameraEvent : LiveData<Boolean>
        get() = _useCameraEvent
    //view password event
    private var _viewPasswordEvent = MutableLiveData<Boolean>()
    val viewPasswordEvent : LiveData<Boolean>
        get() = _viewPasswordEvent

    //initial value for all properties
    init {
        username.value=""
        password.value=""
        missionCode.value=""
        lastLoginTime.value = 0L
        _useCameraEvent.value = false
        _viewPasswordEvent.value = false
    }

    //database query functions
    fun tryLoginEvent() {

        Log.i("Login", "before Login:" + username.value+ " " +password.value)

        uiScope.launch {
            val tryUser = tryLogin() //database.tryLogin(username.value!!,password.value!!)
            if(tryUser!=null && tryUser.userExpirationTimeMilli > System.currentTimeMillis()){
                tryUser.recentLoginTimeMilli = System.currentTimeMillis()
                update(tryUser)
                _validUser.value=tryUser
                onTestRedirect()
            }
            else{
                val newData = LoginUserData()
                newData.userName = username.value!!
                newData.userPassword = password.value!!
                newData.userExpirationTimeMilli = 2* System.currentTimeMillis()
                Log.i("Login", "after Login:" +newData.userName+ " " +newData.userPassword)
                insert(newData)
                onTestSnackbar()
            }
//            val newData = LoginUserData()
//            insert(newData)
//            lastLoginTime.value = newData.recentLoginTimeMilli

        }

    }

    private suspend fun tryLogin(): LoginUserData?{
        return withContext(Dispatchers.IO){
            val tryUser = database.tryLogin(username.value!!,password.value!!)
            tryUser
        }
    }



    private suspend fun insert(data: LoginUserData) {
        withContext(Dispatchers.IO) {
            database.insert(data)
        }
    }

    private suspend fun update(data: LoginUserData) {
        withContext(Dispatchers.IO) {
            database.update(data)
        }
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
    fun onUsingCamera(){
        _useCameraEvent.value = _useCameraEvent.value != true
    }
    fun onViewPassword(){
        _viewPasswordEvent.value = _viewPasswordEvent.value != true
    }



}