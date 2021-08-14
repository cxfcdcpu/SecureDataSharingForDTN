package com.example.securedatasharingfordtn

import android.util.Log
import androidx.lifecycle.ViewModel
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

    private lateinit var publicKey: PublicKey
    private lateinit var privateKey: PrivateKey

    fun bootstrap(dirForPairingFile : String, keyByteArray: ByteArray){
        this.keys = keyByteArray
        this.pairingFileDir = dirForPairingFile

        pairing = PairingFactory.getPairing(this.pairingFileDir)

        val publickeySize = ByteBuffer.wrap(keys,0,4).order(ByteOrder.nativeOrder()).getInt()
        val privatekeySize = ByteBuffer.wrap(keys,publickeySize+4,4).order(ByteOrder.nativeOrder()).getInt()
        Log.i("Shared", "publickey size: "+publickeySize+", privatekey size: "+privatekeySize)
        this.publicKey = PublicKey(Arrays.copyOfRange(this.keys,4,publickeySize+4),this.pairing)
        this.privateKey = PrivateKey(Arrays.copyOfRange(this.keys,8+publickeySize,8+publickeySize+privatekeySize),this.pairing)
    }



}