package com.example.securedatasharingfordtn.login

import android.app.Application
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.securedatasharingfordtn.database.DTNDataSharingDatabaseDao
import com.example.securedatasharingfordtn.database.EntityHelper
import com.example.securedatasharingfordtn.database.LoginUserData
import com.example.securedatasharingfordtn.revoabe.PrivateKey
import com.example.securedatasharingfordtn.revoabe.PublicKey
import com.example.securedatasharingfordtn.revoabe.ReVo_ABE
import com.example.securedatasharingfordtn.tree_type.MembershipTree
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
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

class LoginViewModel(
    val database: DTNDataSharingDatabaseDao,
    application: Application): AndroidViewModel(application) {

    private var keys: ByteArray = byteArrayOf()
    private var pairingFileDir: String = ""
    private lateinit var pairing : Pairing

    private lateinit var publicKey: PublicKey
    private lateinit var privateKey: PrivateKey

    val client = HttpClient(Android) {
        engine {
            connectTimeout = 100_000
            socketTimeout = 100_000
        }
    }

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

    //register error snackbar indicator
    private var _setupOKEvent = MutableLiveData<Boolean>()
    val setupOKEvent: LiveData<Boolean>
        get() = _setupOKEvent
    //try to login
    //login error snackbar indicator functions
    fun doneSetupOKSnackbar(dirForPairingFile : String){
        this.pairingFileDir = dirForPairingFile

        pairing = PairingFactory.getPairing(this.pairingFileDir)

        val publickeySize = ByteBuffer.wrap(keys,0,4).order(ByteOrder.nativeOrder()).getInt()
        val privatekeySize = ByteBuffer.wrap(keys,publickeySize+4,4).order(ByteOrder.nativeOrder()).getInt()
        Log.i("login", "publickey size: "+publickeySize+", privatekey size: "+privatekeySize)
        this.publicKey = PublicKey(Arrays.copyOfRange(this.keys,4,publickeySize+4),this.pairing)
        this.privateKey = PrivateKey(Arrays.copyOfRange(this.keys,8+publickeySize,8+publickeySize+privatekeySize),this.pairing)

        _setupOKEvent.value = false
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
    //Try to setup mission at backend.
    fun trySetupEvent(){

        uiScope.
        launch {
            val response: HttpResponse = client.post("http://131.151.90.204:8081/ReVo_webtest/Bootstrap"){
                body = "{\"username\": \"${username.value}\",\"password\": \"${password.value}\",\"missionCode\":\"${missionCode.value}\"}"
            }

            if(response.status.value==200){
                val channel: ByteReadChannel = response.receive()
                while(!channel.isClosedForRead){
                    val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
                    while(!packet.isEmpty){
                        keys=keys.plus(packet.readBytes())

                    }
                }
                _setupOKEvent.value=true
            }else{
                _registerFailSnackbarEvent.value=true
            }
        }


    }

    private suspend fun tryLogin(): LoginUserData?{
        return withContext(Dispatchers.IO){
            val tryUser = database.tryLogin(username.value!!,password.value!!)
            tryUser
        }
    }

    fun getKeysByteSize() : Int{
        return keys.size
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