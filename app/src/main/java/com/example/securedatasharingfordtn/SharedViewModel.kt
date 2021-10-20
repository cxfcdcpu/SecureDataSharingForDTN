package com.example.securedatasharingfordtn

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.securedatasharingfordtn.database.LoginUserData
import com.example.securedatasharingfordtn.revoabe.PrivateKey
import com.example.securedatasharingfordtn.revoabe.PublicKey
import it.unisa.dia.gas.jpbc.Pairing
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory
import it.unisa.dia.gas.plaf.jpbc.util.Arrays
import java.nio.ByteBuffer
import java.nio.ByteOrder

class SharedViewModel : ViewModel(){

    private var keys: ByteArray = byteArrayOf()
    private var pairingFileDir: String = ""
    private lateinit var pairing : Pairing
    private lateinit var user:LoginUserData
    private lateinit var publicKey: PublicKey
    private lateinit var privateKey: PrivateKey

    private var _keyLive = MutableLiveData<ByteArray>()
    val keyLive: LiveData<ByteArray>
        get() = _keyLive

    private var _userLive = MutableLiveData<LoginUserData>()
    val userLive: LiveData<LoginUserData>
        get() = _userLive

    private var _pairingLive = MutableLiveData<Pairing>()
    val pairingLive: LiveData<Pairing>
        get() = _pairingLive

    private var _pkLive = MutableLiveData<PublicKey>()
    val pkLive: LiveData<PublicKey>
        get() = _pkLive

    private var _prikLive = MutableLiveData<PrivateKey>()
    val prikLive: LiveData<PrivateKey>
        get() = _prikLive

    fun bootstrap(dirForPairingFile : String, user: LoginUserData){
        this.user = user
        this.keys = user.keys
        this.pairingFileDir = dirForPairingFile
        _keyLive.value = keys
        pairing = PairingFactory.getPairing(this.pairingFileDir)
        _pairingLive.value = pairing
        val publickeySize = ByteBuffer.wrap(keys,0,4).order(ByteOrder.nativeOrder()).getInt()
        val privatekeySize = ByteBuffer.wrap(keys,publickeySize+4,4).order(ByteOrder.nativeOrder()).getInt()
        this.publicKey = PublicKey(Arrays.copyOfRange(this.keys,4,publickeySize+4),this.pairing)
        this.privateKey = PrivateKey(Arrays.copyOfRange(this.keys,8+publickeySize,8+publickeySize+privatekeySize),this.pairing)
        _pkLive.value = publicKey
        _prikLive.value = privateKey

        Log.i("Shared", "finished setup shared updated. publicKey size is : "
                +publickeySize+", pivateKey size is: "+ privatekeySize)
    }

    fun getPairing():Pairing{
        return pairing
    }

    fun getPublicKey():PublicKey{
        return publicKey
    }

    fun getPrivateKey():PrivateKey{
        return privateKey
    }

    fun getKeys(): ByteArray{
        return keys
    }

    fun getPairDir(): String {
        return this.pairingFileDir
    }


}