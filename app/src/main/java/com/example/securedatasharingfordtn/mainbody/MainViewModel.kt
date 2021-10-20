package com.example.securedatasharingfordtn.mainbody

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


class MainViewModel(
    val database: DTNDataSharingDatabaseDao,
    application: Application): AndroidViewModel(application) {

    private var keys: ByteArray = byteArrayOf()
    private lateinit var user: LoginUserData
    val client = HttpClient(Android) {
        engine {
            connectTimeout = 100_000
            socketTimeout = 100_000
        }
        install(JsonFeature){
            serializer = GsonSerializer()
        }
    }

    //asyncronized job for database
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)



    //login error snackbar indicator
    private var _directToConnectionEvent = MutableLiveData<Boolean>()
    val directToMainEvent: LiveData<Boolean>
        get() = _directToConnectionEvent

    private var _manageMembers = MutableLiveData<Boolean>()
    val manageMembers: LiveData<Boolean>
        get() = _manageMembers


    //login error snackbar indicator functions
    fun doneDirectToConnectionEvent(){
        _directToConnectionEvent.value = false
    }
    fun onDirectToConnection(){
        _directToConnectionEvent.value = true
    }

    fun setupRevocation(){
        _manageMembers.value = true
    }

    fun doneSetupRevocationEvent(){
        _manageMembers.value = false
    }





}