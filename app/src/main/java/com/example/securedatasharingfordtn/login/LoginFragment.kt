package com.example.securedatasharingfordtn.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.securedatasharingfordtn.R
import com.example.securedatasharingfordtn.database.DTNDataSharingDatabase
import com.example.securedatasharingfordtn.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout

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

        binding.loginViewModel = loginViewModel
        binding.lifecycleOwner = this
        //prevent tab change after rotate the screen
        subscribeTab(binding,loginViewModel)
        observeTabSelection(binding,loginViewModel)
        //show snackbar after login fail
        observeLoginFailEvent(loginViewModel)
        //show snackbar after setup fail
        observeRegisterFailEvent(loginViewModel)

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






}