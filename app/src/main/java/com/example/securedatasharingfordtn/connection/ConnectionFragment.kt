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

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.securedatasharingfordtn.SharedViewModel
import com.example.securedatasharingfordtn.databinding.FragmentConnectionBinding

import kotlinx.android.synthetic.main.fragment_connection.*

class ConnectionFragment : Fragment() {
    companion object {
        private const val LOCATION_PERMISSION_CODE = 100
        private const val READ_PERMISSION_CODE = 101
    }
    //private lateinit var conServiceIntent: Intent
    private lateinit var conService: ConnectionService
    private var conServiceBound: Boolean = false
    lateinit var switchConActButton: Button
    lateinit var switchImgActButton: Button
    private lateinit var binding: FragmentConnectionBinding
    lateinit var sharedModel: SharedViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedModel= ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_PERMISSION_CODE)


    }

    /** Defines callbacks for service binding, passed to bindService()  */
    private val connectionS = object : ServiceConnection {

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
        Log.i("connectionFragment","key byte array size: "+sharedModel.getKeys().size)
        val con_act_intent = Intent(requireContext(), ConnectionActivity::class.java)
        con_act_intent.putExtra("keys", sharedModel.getKeys());
        con_act_intent.putExtra("pairingDir", sharedModel.getPairDir());
        startActivity(con_act_intent)
    }

    /** Called when the user taps the "Images" button  */
    private fun switchImgAct() {
        val img_act_intent = Intent(requireContext(), ImageActivity::class.java)
        img_act_intent.putExtra("parent", "MainActivity")
        startActivity(img_act_intent)
    }

    /** Called when the user taps the "Connection" switch  */
    private fun switchConService(isChecked: Boolean) {
        if (isChecked) {
            Toast.makeText(requireContext(), "Nearby Devices Searching", Toast.LENGTH_SHORT).show()
            //conServiceIntent = Intent(this, ConnectionService::class.java)
            //startService(conServiceIntent)
            Intent(requireContext(), ConnectionService::class.java).also { intent ->
                intent.putExtra("keys", sharedModel.getKeys());
                intent.putExtra("pairingDir", sharedModel.getPairDir());
                requireActivity().bindService(intent, connectionS, Context.BIND_AUTO_CREATE)
            }
            switchConActButton.isEnabled = true

        } else {
            Toast.makeText(requireContext(), "Closing Connection", Toast.LENGTH_SHORT).show()
            //stopService(conServiceIntent)
            requireActivity().unbindService(connectionS)
            switchConActButton.isEnabled = false
        }
    }

    /** Function to check and request permission.*/
    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(requireContext(),permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(permission), requestCode)
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
                Toast.makeText(requireContext(),"Read Storage Permission Granted", Toast.LENGTH_SHORT).show()
                switchImgAct()
            } else {
                Toast.makeText(requireContext(),"Read Storage Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConnectionBinding.inflate(inflater)


        //Switch to connection activity
        switchConActButton = binding.switchConnectionActivity
        switchConActButton.isEnabled = false
        switchConActButton.setOnClickListener {
            switchConAct()
        }

        //Switch to image activity
        switchImgActButton = binding.switchImageActivity
        switchImgActButton.setOnClickListener {
            checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_PERMISSION_CODE)
        }

        //Switch Connection on and off
        val connectionOnOffSwitch: Switch =binding.connectionOnOff
        connectionOnOffSwitch.setOnCheckedChangeListener { _,
                                                           isChecked -> switchConService(isChecked)
        }


        return binding.root
    }


}