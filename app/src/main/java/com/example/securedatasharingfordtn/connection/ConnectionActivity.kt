package com.example.securedatasharingfordtn.connection

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Environment
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.securedatasharingfordtn.R
import com.example.securedatasharingfordtn.SharedViewModel
import com.example.securedatasharingfordtn.revoabe.ReVo_ABE
import java.io.File


class ConnectionActivity : AppCompatActivity(), ConnectionService.ServiceCallbacks {

    companion object {
        private const val SEND_IMAGE_ACTIVITY_REQUEST_CODE = 1
    }

    private lateinit var conService: ConnectionService
    private var conServiceBound: Boolean = false

    //lateinit var listElementsArrayList: List<String>
    private lateinit var selectedEndPointName: String

    lateinit var adapter: ArrayAdapter<String>
    lateinit var listview: ListView
    var listElementsArrayList: ArrayList<String> = ArrayList()
    lateinit var sharedModel:SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connection)

        //Switch to Main activity
        val backConActButton: Button = findViewById(R.id.back_connection_activity);
        backConActButton.setOnClickListener {
            backConAct()
        }
        sharedModel= ViewModelProvider(this).get(SharedViewModel::class.java)
        //declare the connected devices listview
        listview = findViewById(R.id.connection_list)
        adapter = ArrayAdapter(this,android.R.layout.simple_expandable_list_item_1,listElementsArrayList)
        listview.adapter = adapter
        listview.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                switchImgAct(position)
            }
        }
    }

    /** Called when the user taps the back button  */
    private fun backConAct() {
        finish()
    }

    // select a receiver to see image list
    private fun switchImgAct(position: Int) {
        selectedEndPointName = listview.getItemAtPosition(position) as String

        val img_act_intent = Intent(this, ImageActivity::class.java)
        img_act_intent.putExtra("parent", "ConnectionActivity")
        img_act_intent.putExtra("recipient", selectedEndPointName)

        startActivityForResult(img_act_intent, SEND_IMAGE_ACTIVITY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SEND_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val fileName = data!!.getStringExtra("fileName")!!
                val folder = data!!.getStringExtra("folder")!!
                val photoFile = getPhotoFileUri(fileName, folder)
                sendImageFile(fileName, photoFile)
            }
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

    override fun refreshConnectionList(endpointNameConnected: ArrayList<String>) {
        listElementsArrayList.clear()
        listElementsArrayList.addAll(endpointNameConnected)
        adapter.notifyDataSetChanged();
    }

    private fun sendImageFile(fileName: String, photoFile: File) {
        Toast.makeText(applicationContext, "Sending to $selectedEndPointName", Toast.LENGTH_LONG).show()
        conService.textMsg = fileName
        conService.imageMsg = photoFile
        var RL = listOf<Int>()
        conService.encrypteFilename = ReVo_ABE.encrypt(sharedModel.getPairing()
                    ,sharedModel.getPublicKey(),fileName.toByteArray(),"A", RL)

        conService.setConnection(selectedEndPointName)
    }

    override fun onStart() {
        super.onStart()
        // Bind to LocalService
        Intent(this, ConnectionService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        if (conServiceBound) {
            conService.setCallbacks(null) //unregister
            unbindService(connection)
            conServiceBound = false;
        }
    }


    /** Defines callbacks for service binding, passed to bindService()  */
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as ConnectionService.ConServiceBinder
            conService = binder.getService()
            conServiceBound = true
            conService.setCallbacks(this@ConnectionActivity); // register

            //initial list load
            refreshConnectionList(conService.endpointNameConnected)
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            conServiceBound = false
        }
    }
}