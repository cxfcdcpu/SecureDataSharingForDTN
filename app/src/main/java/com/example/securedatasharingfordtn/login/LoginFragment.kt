package com.example.securedatasharingfordtn.login

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.securedatasharingfordtn.R
import com.example.securedatasharingfordtn.database.DTNDataSharingDatabase
import com.example.securedatasharingfordtn.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


private const val PERMISSIONS_REQUEST_CODE = 10
private val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.CAMERA)
class LoginFragment : Fragment()  {

    private lateinit var container: ConstraintLayout
    private lateinit var viewFinder: CodeScanner
    private lateinit var outputDirectory: File
    private lateinit var broadcastManager: LocalBroadcastManager
    private lateinit var binding: FragmentLoginBinding
    private var displayId: Int = -1
    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK
    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private lateinit var windowManager: WindowManager
    /** Blocking camera operations are performed using this executor */
    private lateinit var cameraExecutor: ExecutorService


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        cameraExecutor = Executors.newSingleThreadExecutor()
        //data binding and view model reference obj
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_login,container,false
        )
        val application = requireNotNull(this.activity).application

        val dataSource = DTNDataSharingDatabase.getInstance(application).dataSharingDatabaseDao

        val viewModelFactory = LoginViewModelFactory(dataSource, application)

        val loginViewModel = ViewModelProvider(
            this,viewModelFactory).get(LoginViewModel::class.java)
        viewFinder = CodeScanner(requireContext(), binding.viewFinder)
        binding.loginViewModel = loginViewModel
        binding.lifecycleOwner = this
        //prevent tab change after rotate the screen
        subscribeTab(binding,loginViewModel)
        observeTabSelection(binding,loginViewModel)
        //show snackbar after login fail
        observeLoginFailEvent(loginViewModel)
        //show snackbar after setup fail
        observeRegisterFailEvent(loginViewModel)
        //use camera when camera clicked
        observeCameraEvent(loginViewModel)
        //view password
        observeViewPasswordEvent(loginViewModel)


        return binding.root
    }


    fun subscribeTab(binding: FragmentLoginBinding,loginViewModel: LoginViewModel){
        binding.loginSetupTab.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                loginViewModel.tabSelect.value = binding.loginSetupTab.selectedTabPosition==0
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }

        })
    }

    fun observeTabSelection(binding: FragmentLoginBinding,loginViewModel: LoginViewModel){
        loginViewModel.tabSelect.observe(viewLifecycleOwner, Observer {
            if(it == true){
                binding.button.visibility=View.VISIBLE
                binding.button2.visibility=View.INVISIBLE
                binding.textView4.visibility=View.INVISIBLE
                binding.editTextNumberPassword.visibility=View.INVISIBLE
                binding.imageButton.visibility = View.INVISIBLE
                if(binding.loginSetupTab.selectedTabPosition!=0){
                    binding.loginSetupTab.getTabAt(0)?.select()
                }

            }
            else{
                binding.button.visibility=View.INVISIBLE
                binding.button2.visibility=View.VISIBLE
                binding.textView4.visibility=View.VISIBLE
                binding.editTextNumberPassword.visibility=View.VISIBLE
                binding.imageButton.visibility=View.VISIBLE
                if(binding.loginSetupTab.selectedTabPosition!=1){
                    binding.loginSetupTab.getTabAt(1)?.select()
                }
            }
        })
    }

    fun observeLoginFailEvent(loginViewModel: LoginViewModel){
        loginViewModel.loginFailSnackbarEvent.observe(viewLifecycleOwner, Observer {
            if (it == true) { // Observed state is true.
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    getString(R.string.snackbar_test_text)+loginViewModel.lastLoginTime.value.toString(),
                    Snackbar.LENGTH_SHORT // How long to display the message.
                ).show()
                // Reset state to make sure the snackbar is only shown once, even if the device
                // has a configuration change.
                loginViewModel.doneShowingLoginSnackbar()
            }
        })
    }

    fun observeRegisterFailEvent(loginViewModel: LoginViewModel){
        loginViewModel.registerFailSnackbarEvent.observe(viewLifecycleOwner, Observer {
            if (it == true) { // Observed state is true.
                this.findNavController().navigate(
                    LoginFragmentDirections.actionLoginFragmentToMainFragment())
                // Reset state to make sure the snackbar is only shown once, even if the device
                // has a configuration change.
                loginViewModel.doneShowingRegisterSnackbar()
            }
        })
    }

    fun observeCameraEvent(loginViewModel: LoginViewModel){
        //also give bind the viewFinder with click ability
        binding.viewFinder.setOnClickListener {
            viewFinder.startPreview()
        }
        loginViewModel.useCameraEvent.observe(viewLifecycleOwner, Observer {
            if(it == true){
                binding.viewFinder.visibility=View.VISIBLE
                if (!hasPermissions(requireContext())) {
                    // Request camera-related permissions
                    requestPermissions(PERMISSIONS_REQUIRED, PERMISSIONS_REQUEST_CODE)
                } else {
                    // If permissions have already been granted, proceed

                    startCamera()
                }
            }
            else{
                binding.viewFinder.visibility=View.INVISIBLE
                cameraExecutor.shutdown()

            }
        })
    }

    fun observeViewPasswordEvent(loginViewModel: LoginViewModel){

        loginViewModel.viewPasswordEvent.observe(viewLifecycleOwner, Observer {
            if(it == true){
                binding.editTextTextPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.editTextNumberPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            }else{
                binding.editTextTextPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.editTextNumberPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        })

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (PackageManager.PERMISSION_GRANTED == grantResults.firstOrNull()) {
                // Take the user to the success fragment when permission is granted
                Toast.makeText(context, "Permission request granted", Toast.LENGTH_LONG).show()

                startCamera()
            } else {
                Toast.makeText(context, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun takePhoto() {}

//    private fun startCamera() {
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
//        cameraProviderFuture.addListener(Runnable {
//
//            // CameraProvider
//            cameraProvider = cameraProviderFuture.get()
//
//            // Select lensFacing depending on the available cameras
//            lensFacing = when {
//                hasBackCamera() -> CameraSelector.LENS_FACING_BACK
//                hasFrontCamera() -> CameraSelector.LENS_FACING_FRONT
//                else -> throw IllegalStateException("Back and front camera are unavailable")
//            }
//
//            // Enable or disable switching between cameras
//            //updateCameraSwitchButton()
//
//            // Build and bind the camera use cases
//            bindCameraUseCases()
//            viewFinder.visibility=View.VISIBLE
//        }, ContextCompat.getMainExecutor(requireContext()))
//
//    }


    private fun startCamera(){

        viewFinder.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        viewFinder.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        viewFinder.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        viewFinder.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        viewFinder.isAutoFocusEnabled = true // Whether to enable auto focus or not
        viewFinder.isFlashEnabled = false // Whether to enable flash or not


        viewFinder.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            activity?.runOnUiThread {
                Toast.makeText(context, "Camera initialization error: ${it.message}", Toast.LENGTH_LONG).show()
            }
        }
// Callbacks
        viewFinder.decodeCallback = DecodeCallback {
            activity?.runOnUiThread {
                //Toast.makeText(context, "Scan result: ${it.text}", Toast.LENGTH_LONG).show()
                var num: Boolean = it.text.matches("-?\\d+(\\.\\d+)?".toRegex())
                if(num){
                    binding.editTextNumberPassword.setText(it.text)
                    binding.imageButton.performClick()
                }else{
                    viewFinder.startPreview()
                }

            }
        }
        viewFinder.startPreview()
    }





    /** Declare and bind preview, capture and analysis use cases */
//    private fun bindCameraUseCases() {
//
//
//        val rotation = viewFinder.display.rotation
//
//        // CameraProvider
//        val cameraProvider = cameraProvider
//            ?: throw IllegalStateException("Camera initialization failed.")
//
//        // CameraSelector
//        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
//
//        // Preview
//        preview = Preview.Builder()
//            // We request aspect ratio but no resolution
//            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
//            // Set initial target rotation
//            .setTargetRotation(rotation)
//            .build()
//
//        // ImageCapture
//        imageCapture = ImageCapture.Builder()
//            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
//            // We request aspect ratio but no resolution to match preview config, but letting
//            // CameraX optimize for whatever specific resolution best fits our use cases
//            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
//            // Set initial target rotation, we will have to call this again if rotation changes
//            // during the lifecycle of this use case
//            .setTargetRotation(rotation)
//            .build()
//
//
//
//        // Must unbind the use-cases before rebinding them
//        cameraProvider.unbindAll()
//
//        try {
//            // A variable number of use-cases can be passed here -
//            // camera provides access to CameraControl & CameraInfo
//            camera = cameraProvider.bindToLifecycle(
//                this, cameraSelector, preview, imageCapture)
//
//            // Attach the viewfinder's surface provider to preview use case
//            preview?.setSurfaceProvider(viewFinder.surfaceProvider)
//        } catch (exc: Exception) {
//            Log.e(TAG, "Use case binding failed", exc)
//        }
//    }

//
//    /** Returns true if the device has an available back camera. False otherwise */
//    private fun hasBackCamera(): Boolean {
//        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) ?: false
//    }
//
//    /** Returns true if the device has an available front camera. False otherwise */
//    private fun hasFrontCamera(): Boolean {
//        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) ?: false
//    }
//
//
    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"

        fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

}