package com.example.securedatasharingfordtn.login

import android.app.Application
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.securedatasharingfordtn.HelperFunctions
import com.example.securedatasharingfordtn.database.DTNDataSharingDatabaseDao
import com.example.securedatasharingfordtn.database.EntityHelper
import com.example.securedatasharingfordtn.database.LoginUserData
import com.example.securedatasharingfordtn.http.KtorHttpClient
import com.example.securedatasharingfordtn.revoabe.PrivateKey
import com.example.securedatasharingfordtn.revoabe.PublicKey
import com.example.securedatasharingfordtn.revoabe.ReVo_ABE
import com.example.securedatasharingfordtn.tree_type.MembershipTree
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.json.*
import kotlinx.coroutines.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import it.unisa.dia.gas.jpbc.Pairing
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory
import it.unisa.dia.gas.plaf.jpbc.util.Arrays
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.sql.Timestamp
import com.google.gson.Gson
import com.google.gson.GsonBuilder


class LoginViewModel(
    val database: DTNDataSharingDatabaseDao,
    application: Application): AndroidViewModel(application) {

    private var keys: ByteArray = byteArrayOf()
    var members: String = ""
    private lateinit var user: LoginUserData

    val client = KtorHttpClient.KtorClient

    //asyncronized job for database
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)


    //properties
    val username = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val missionCode = MutableLiveData<String>()
    val lastLoginTime = MutableLiveData<Long>()
    var tabSelect = MutableLiveData<Boolean>()

    //login error snackbar indicator
    private var _directToMainEvent = MutableLiveData<Boolean>()
    val directToMainEvent: LiveData<Boolean>
        get() = _directToMainEvent
    //login error snackbar indicator functions
    fun doneDirectToMainEvent(){
        _directToMainEvent.value = false
    }

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

    //register error snackbar indicator
    private var _setupOKEvent = MutableLiveData<Boolean>()
    val setupOKEvent: LiveData<Boolean>
        get() = _setupOKEvent
    //try to login
    //login error snackbar indicator functions
    fun doneSetupOKSnackbar(){

        runBlocking {
            fetchUserFromServer()

        }
        runBlocking {
            launch {
                if (user != null) {
                    insert(user)
                    Log.i("Login", "insert into database successcully")
                } else {
                    Log.i("Login", "can't insert User to the database")
                }

            }
        }

        _setupOKEvent.value = false
        onTestRedirect()

    }



    //when user click setup button
    private var _onSetupEvent = MutableLiveData<Boolean>()
    val onSetupEvent: LiveData<Boolean>
        get() = _onSetupEvent

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

        Log.i("Login", "before Login:" + username.value!!.trim()+ " " +password.value)

        runBlocking {
            val tryUser = tryLogin() //database.tryLogin(username.value!!,password.value!!)

            //add user with none expired date.
            if(tryUser!=null && (true || tryUser.expirationDate > System.currentTimeMillis())){
                runBlocking {
                    tryUser.recentLoginTimeMilli = System.currentTimeMillis()
                    update(tryUser)
                    user = tryUser
                    members = user.members
                }

                Log.i("Login", "find user in the database")
                onTestRedirect()
            }else{
                onTestSnackbar();
            }

        }

    }
    //Try to setup mission at backend.
    fun trySetupEvent(){

        uiScope.
        launch {
            val response: HttpResponse = client.post(KtorHttpClient.BASE_URL+KtorHttpClient.APIName+"Bootstrap"){
                body = "{\"username\": \"${username.value!!.trim()}\",\"password\": \"${password.value}\",\"missionCode\":\"${missionCode.value}\"}"
            }

            if(response.status.value==200){
                keys = response.readBytes()
                _setupOKEvent.value=true
            }else{
                _registerFailSnackbarEvent.value=true
            }


        }
    }

    private suspend fun fetchUserFromServer(){


        runBlocking {
            launch {
                val response: HttpResponse =
                    client.post(KtorHttpClient.BASE_URL+KtorHttpClient.APIName+"SearchUser") {
                        body =
                            "{\"username\": \"${username.value!!.trim()}\", \"password\": \"${password.value}\"}"
                    }

                val response2: HttpResponse = client.post(KtorHttpClient.BASE_URL+KtorHttpClient.APIName+"GetUsersStringOfAMission"){
                    body = "{\"missionCode\":\"${missionCode.value}\"}"
                }

                if(response2.status.value==200){
                    members = response2.readText()
                }else{
                    _registerFailSnackbarEvent.value=true
                }

                if (response.status.value == 200) {
                    val userInfo: UserInfo = response.receive()
                    val rt = HelperFunctions.convertDateStringToLong(userInfo.registerTime)
                    val et = HelperFunctions.convertDateStringToLong(userInfo.expirationDate)

                    if (rt!=0L && et != 0L) {
                        user = LoginUserData(
                            username = userInfo.username,
                            password = userInfo.password,
                            mission = missionCode.value!!.toLong(),
                            attributes = userInfo.attributesString,
                            recentLoginTimeMilli = System.currentTimeMillis(),
                            registerationTime = rt,
                            expirationDate = et,
                            members = members,
                            keys = keys
                        )
                        Log.i("Login", "Private user: " + userInfo.string)
                    }

                }
            }
        }
    }



    private suspend fun tryLogin(): LoginUserData?{
        return withContext(Dispatchers.IO){
            val tryUser = database.tryLogin(username.value!!.trim(),password.value!!)
            tryUser
        }
    }

    fun getKeysByteSize() : Int{
        return keys.size
    }


    fun getKeys(): ByteArray{
        return this.keys
    }

    fun getUser(): LoginUserData{
        return this.user
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
        _directToMainEvent.value = true
    }
    fun onUsingCamera(){
        _useCameraEvent.value = _useCameraEvent.value != true
    }
    fun onViewPassword(){
        _viewPasswordEvent.value = _viewPasswordEvent.value != true
    }





}