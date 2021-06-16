package com.example.securedatasharingfordtn.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.securedatasharingfordtn.R
import com.example.securedatasharingfordtn.database.DTNDataSharingDatabase
import com.example.securedatasharingfordtn.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar

class LoginFragment : Fragment()  {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //data binding and view model reference obj
        val binding: FragmentLoginBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_login,container,false
        )
        val application = requireNotNull(this.activity).application

        val dataSource = DTNDataSharingDatabase.getInstance(application).dataSharingDatabaseDao

        val viewModelFactory = LoginViewModelFactory(dataSource, application)

        val loginViewModel = ViewModelProvider(
            this,viewModelFactory).get(LoginViewModel::class.java)

        binding.loginForm.loginViewModel = loginViewModel

        binding.lifecycleOwner = this

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

        loginViewModel.registerFailSnackbarEvent.observe(viewLifecycleOwner, Observer {
            if (it == true) { // Observed state is true.
                this.findNavController().navigate(
                    LoginFragmentDirections.actionLoginFragmentToMainFragment())
                // Reset state to make sure the snackbar is only shown once, even if the device
                // has a configuration change.
                loginViewModel.doneShowingLoginSnackbar()
            }
        })


        return binding.root
    }
}