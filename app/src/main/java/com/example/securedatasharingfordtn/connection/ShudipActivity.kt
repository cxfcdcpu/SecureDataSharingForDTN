package com.example.securedatasharingfordtn.connection
import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.securedatasharingfordtn.R
import com.example.securedatasharingfordtn.SharedViewModel

class ShudipActivity : AppCompatActivity() {
    companion object {
        private const val LOCATION_PERMISSION_CODE = 100
        private const val READ_PERMISSION_CODE = 101
    }
    //private lateinit var conServiceIntent: Intent
    private lateinit var conService: ConnectionService
    private var conServiceBound: Boolean = false
    lateinit var switchConActButton: Button
    lateinit var switchImgActButton: Button
    lateinit var sharedKeys: ByteArray
    lateinit var sharedDir: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shudip)
        // Function to check and request permission.
        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_PERMISSION_CODE)
        val bundle = intent.extras
        sharedDir = bundle!!.getString("pairingDir").toString()
        sharedKeys = bundle.getByteArray("keys")!!

        //Switch to connection activity
        switchConActButton = findViewById(R.id.switch_connection_activity)
        switchConActButton.isEnabled = false
        switchConActButton.setOnClickListener {
            switchConAct()
        }

        //Switch to image activity
        switchImgActButton = findViewById(R.id.switch_image_activity)
        switchImgActButton.setOnClickListener {
            checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_PERMISSION_CODE)
        }
        Log.i("Shudip", "11111111111111111222222222222333333333333"+ sharedDir)
        //Switch Connection on and off
        val connectionOnOffSwitch: Switch = findViewById(R.id.connection_on_off)
        connectionOnOffSwitch.setOnCheckedChangeListener { _,
                isChecked -> switchConService(isChecked)
        }
    }

    /** Defines callbacks for service binding, passed to bindService()  */
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as ConnectionService.ConServiceBinder
            conService = binder.getService()
            conServiceBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            conServiceBound = false
        }
    }


    /** Called when the user taps the "Connected Device" button  */
    private fun switchConAct() {
        val con_act_intent = Intent(this, ConnectionActivity::class.java)
        con_act_intent.putExtra("keys", sharedKeys);
        con_act_intent.putExtra("pairingDir", sharedDir);
        startActivity(con_act_intent)
    }

    /** Called when the user taps the "Images" button  */
    private fun switchImgAct() {
        val img_act_intent = Intent(this, ImageActivity::class.java)
        img_act_intent.putExtra("parent", "MainActivity")
        startActivity(img_act_intent)
    }

    /** Called when the user taps the "Connection" switch  */
    private fun switchConService(isChecked: Boolean) {
        if (isChecked) {

            Toast.makeText(this, "Nearby Devices Searching", Toast.LENGTH_SHORT).show()
            //conServiceIntent = Intent(this, ConnectionService::class.java)
            //startService(conServiceIntent)
            Intent(this, ConnectionService::class.java).also { intent ->
                intent.putExtra("keys", sharedKeys);
                intent.putExtra("pairingDir", sharedDir);
                bindService(intent, connection, Context.BIND_AUTO_CREATE)
            }
            switchConActButton.isEnabled = true

        } else {
            Toast.makeText(this, "Closing Connection", Toast.LENGTH_SHORT).show()
            //stopService(conServiceIntent)
            unbindService(connection)
            switchConActButton.isEnabled = false
        }
    }

    /** Function to check and request permission.*/
    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this,permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        }
        else {
            if (requestCode == LOCATION_PERMISSION_CODE) {
            }

            if (requestCode == READ_PERMISSION_CODE) {
                //Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show()
                switchImgAct()
            }
        }
    }

    /** This function is called when the user accepts or decline the permission.*/
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this,"Location Permission Granted",Toast.LENGTH_SHORT).show()
            } else {
                //
            }
        } else if (requestCode == READ_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,"Read Storage Permission Granted",Toast.LENGTH_SHORT).show()
                switchImgAct()
            } else {
                Toast.makeText(this,"Read Storage Permission Denied",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}