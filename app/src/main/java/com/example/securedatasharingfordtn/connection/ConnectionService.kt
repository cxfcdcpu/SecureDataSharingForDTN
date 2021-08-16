package com.example.securedatasharingfordtn.connection

import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.collection.SimpleArrayMap
import androidx.core.net.toUri
import com.example.securedatasharingfordtn.revoabe.Ciphertext
import com.google.android.gms.common.util.IOUtils.copyStream
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets


/***check service condition on force stop***/

class ConnectionService : Service() {

    val TAG = "NearbyService"
    val SERVICE_ID = "AFRL_project"
    lateinit var DEVICE_ID: String
    private val STRATEGY: Strategy = Strategy.P2P_CLUSTER
    private val context: Context = this

    var endpointIDConnected: ArrayList<String> = ArrayList()
    var endpointNameConnected: ArrayList<String> = ArrayList()
    var endpointIDNameMap: HashMap<String, String> = HashMap()
    var endpointNameIDMap: HashMap<String, String> = HashMap()

    private val conServiceBinder: IBinder = ConServiceBinder()
    private var serviceCallbacks: ServiceCallbacks? = null

    var textMsg: String? = null
    var encrypteFilename: Ciphertext? = null
    var imageMsg: File? = null
    var rcvdFilename: String? = null
    private val incomingFilePayloads = SimpleArrayMap<Long, Payload>()
    private val completedFilePayloads = SimpleArrayMap<Long, Payload>()
    private val filePayloadFilenames = SimpleArrayMap<Long, String>()


    interface ServiceCallbacks {
        fun refreshConnectionList(endpointNameConnected: ArrayList<String>)
    }
    fun setCallbacks(callbacks: ServiceCallbacks?) {
        if (callbacks != null) {
            serviceCallbacks = callbacks
        }
    }

    inner class ConServiceBinder : Binder() {
        // Return this instance of MyService so clients can call public methods
        fun getService(): ConnectionService = this@ConnectionService
    }

    override fun onBind(intent: Intent): IBinder {
        //TODO("Return the communication channel to the service.")
        return conServiceBinder
    }

    override fun onCreate() {
        Log.d(TAG, "Inside onCreate ConnectionService")
        DEVICE_ID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID)
        advertiserHandler.post(advertiserRunnable)
        discoveryHandler.post(discoverRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Inside onDestroy ConnectionService")
        Nearby.getConnectionsClient(context).stopAllEndpoints()
        discoveryHandler.removeCallbacks(discoverRunnable)
        advertiserHandler.removeCallbacks(advertiserRunnable)
        stopSelf()
    }

    var advertiserHandler: Handler = Handler(Looper.getMainLooper())
    var advertiserRunnable = Runnable {
        Log.d(TAG, "Inside advertiser runnable")
        startAdvertising()
    }

    var discoveryHandler: Handler = Handler(Looper.getMainLooper())
    var discoverRunnable = Runnable {
        Log.d(TAG, "Inside Runnable searching")
        startDiscovery()
    }

    private fun startAdvertising() {
        val advertisingOptions = AdvertisingOptions.Builder().setStrategy(STRATEGY).build()
        Nearby.getConnectionsClient(context)
            .startAdvertising(
                DEVICE_ID, SERVICE_ID, connectionLifeCycleCallback, advertisingOptions
            )
            .addOnSuccessListener(
                OnSuccessListener { unused: Void? ->
                    Log.d(TAG, "We're advertising!")
                })
            .addOnFailureListener(
                OnFailureListener { e: Exception? ->
                    Log.d(TAG, "We were unable to start advertising.")
                })
    }

    private fun startDiscovery() {
        val discoveryOptions = DiscoveryOptions.Builder().setStrategy(STRATEGY).build()
        Nearby.getConnectionsClient(context)
            .startDiscovery(SERVICE_ID, endpointDiscoveryCallback, discoveryOptions)
            .addOnSuccessListener { unused: Void? ->
                Log.d(TAG, "We're discovering!")
            }
            .addOnFailureListener { e: Exception? ->
                Log.d(TAG, "We were unable to start discovering!")
            }
    }

    private val endpointDiscoveryCallback: EndpointDiscoveryCallback =
        object : EndpointDiscoveryCallback() {
            override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
                // An endpoint was found. We request a connection to it.
                if (info.endpointName != DEVICE_ID && !endpointIDConnected.contains(endpointId)) {
                    endpointIDConnected.add(endpointId)
                    endpointNameConnected.add(info.endpointName)
                    endpointIDNameMap[endpointId] = info.endpointName
                    endpointNameIDMap[info.endpointName] = endpointId
                    Log.d("Test", info.endpointName)
                    serviceCallbacks?.refreshConnectionList(endpointNameConnected)
                }
            }

            override fun onEndpointLost(endpointId: String) {
                Log.d(TAG, "A previously discovered endpoint has gone away.")
                if (endpointIDConnected.contains(endpointId)) {
                    endpointIDConnected.remove(endpointId)
                    endpointNameConnected.remove(endpointIDNameMap[endpointId])
                    endpointIDNameMap.remove(endpointId) //so many ids will be generated in every second
                    //endpointNameIDMap.remove(info.endpointName) //don't bother, device is limited
                    serviceCallbacks?.refreshConnectionList(endpointNameConnected)
                }
            }
        }

    fun setConnection(endpointName: String) {
        Nearby.getConnectionsClient(context).requestConnection(
            "",
            endpointNameIDMap[endpointName].toString(),
            connectionLifeCycleCallback
        )
        .addOnSuccessListener { unused: Void? ->
            Log.d(TAG,"We successfully requested a connection. Now both sides must accept before the connection is established.")
        }
        .addOnFailureListener { e: java.lang.Exception? ->
            Log.d(TAG, "Nearby Connections failed to request the connection.")
        }
    }

    private val connectionLifeCycleCallback: ConnectionLifecycleCallback =
        object : ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
                // Automatically accept the connection on both sides.
                Log.d(TAG, "Inside onConnectionInitiated")
                Nearby.getConnectionsClient(context).acceptConnection(endpointId, payloadCallback)
            }

            override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
                when (result.status.statusCode) {
                    ConnectionsStatusCodes.STATUS_OK -> {
                        Log.d(TAG, "We're connected! Can now start sending and receiving data.")
                        //endpointsConnected.add(endpointId)
                        //for example, send the initial (must) information here
                        //sendPayload(endpointId, "Random".toByteArray())

//                        if (textMsg != null) {
//                            sendPayload(endpointId, textMsg!!.toByteArray())
//                            textMsg = null
//                        }
                        sendPayload(endpointId)
                    }
                    ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED ->
                        Log.d(TAG,"The connection was rejected by one or both sides.")
                    ConnectionsStatusCodes.STATUS_ERROR ->
                        Log.d( TAG,"The connection broke before it was able to be accepted.")
                    else ->
                        Log.d(TAG, "Unknown status code")
                }
            }

            override fun onDisconnected(endpointId: String) {
                Log.d(TAG,"We've been disconnected from this endpoint. No more data can be sent or received.")
            }
        }

//    private fun sendPayload(endpointId: String, bytes: ByteArray) {
//        val payload = Payload.fromBytes(bytes)
//        Nearby.getConnectionsClient(applicationContext).sendPayload(endpointId, payload)
//    }

    private fun sendPayload(endpointId: String) {
        if (textMsg != null) {

            val filenamePayload = Payload.fromBytes(encrypteFilename!!.toByteArray())
            Nearby.getConnectionsClient(context).sendPayload(endpointId, filenamePayload)
            textMsg = null
        }
        if (imageMsg != null) {
            val pfd = contentResolver.openFileDescriptor(imageMsg!!.toUri(), "r")
            val filePayload = Payload.fromFile(pfd!!)
            Nearby.getConnectionsClient(applicationContext).sendPayload(endpointId, filePayload)
            imageMsg = null
        }
    }

    private val payloadCallback: PayloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            // A new payload is being sent over.
            Log.d(TAG, "Payload Received")
            when (payload.type) {
                Payload.Type.BYTES -> {
                    rcvdFilename = String(payload.asBytes()!!, StandardCharsets.UTF_8)
                }
                Payload.Type.FILE -> {
                    // Add this to our tracking map, so that we can retrieve the payload later.
                    incomingFilePayloads.put(payload.id, payload);
                }
                Payload.Type.STREAM -> {
                    Log.d(TAG, "Inside file mode")
                }
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            if (update.status == PayloadTransferUpdate.Status.SUCCESS) {
                val payloadId = update.payloadId
                val payload = incomingFilePayloads.remove(payloadId)
                completedFilePayloads.put(payloadId, payload)
                if (payload != null && payload.type == Payload.Type.FILE) {
                    val isDone = processFilePayload(payloadId)
                    if (isDone) {
                        Nearby.getConnectionsClient(context).disconnectFromEndpoint(endpointId)
                    }
                }
            }
        }

        private fun processFilePayload(payloadId: Long): Boolean {
            // BYTES and FILE could be received in any order, so we call when either the BYTES or the FILE
            // payload is completely received. The file payload is considered complete only when both have
            // been received.
            val filePayload = completedFilePayloads[payloadId]
            if (filePayload != null && rcvdFilename != null) {
                completedFilePayloads.remove(payloadId)
                filePayloadFilenames.remove(payloadId)

                // Get the received file (which will be in the Downloads folder)
                // Because of https://developer.android.com/preview/privacy/scoped-storage, we are not
                // allowed to access filepaths from another process directly. Instead, we must open the
                // uri using our ContentResolver.
                val uri: Uri? = filePayload.asFile()!!.asUri()
                try {
                    // Copy the file to a new location.
                    val `in`: InputStream? = context.contentResolver.openInputStream(uri!!)
                    //copyStream(`in`, FileOutputStream(File(context.cacheDir, rcvdFilename)))
                    val movedFile = getPhotoFileUri(rcvdFilename!!, "collected_images")
                    copyStream(`in`, FileOutputStream(movedFile))
                } catch (e: IOException) {
                    // Log the error.
                } finally {
                    // Delete the original file.
                    context.contentResolver.delete(uri!!, null, null)
                    Toast.makeText(applicationContext, "File Received $rcvdFilename", Toast.LENGTH_LONG).show()
                    rcvdFilename = null
                }

                return true
            }
            return false
        }

    }
    // Returns the File for a photo stored on disk given the fileName
    fun getPhotoFileUri(fileName: String, folder: String): File {
        // Use `getExternalFilesDir` on Context to access package-specific directories. // This way, we don't need to request external read/write runtime permissions.
        val mediaStorageDir = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), folder)

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d("ConnectionActivity", "failed to create directory")
        }

        return File(mediaStorageDir.getPath() + File.separator.toString() + fileName)
    }
}