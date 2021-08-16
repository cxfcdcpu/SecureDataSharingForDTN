package com.example.securedatasharingfordtn.mainbody

import android.os.Bundle
import android.telecom.ConnectionService
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.budiyev.android.codescanner.CodeScanner
import com.example.securedatasharingfordtn.R
import com.example.securedatasharingfordtn.SharedViewModel
import com.example.securedatasharingfordtn.database.DTNDataSharingDatabase
import com.example.securedatasharingfordtn.databinding.FragmentLoginBinding
import com.example.securedatasharingfordtn.databinding.FragmentMainBinding
import com.example.securedatasharingfordtn.login.LoginViewModel
import it.unisa.dia.gas.jpbc.*
import it.unisa.dia.gas.plaf.jpbc.pairing.*
import com.example.securedatasharingfordtn.login.*
import kotlinx.android.synthetic.main.fragment_login.*
import java.io.InputStream
class MainFragment : Fragment(){

    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_main,container,false
        )
        val application = requireNotNull(this.activity).application

        val dataSource = DTNDataSharingDatabase.getInstance(application).dataSharingDatabaseDao

        val viewModelFactory = MainViewModelFactory(dataSource, application)
        var sharedModel=ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        val mainViewModel = ViewModelProvider(
            this,viewModelFactory).get(MainViewModel::class.java)
        binding.mainViewModel = mainViewModel
        binding.lifecycleOwner = this

        observeDirectToMainEvent(mainViewModel)
        return binding.root
    }



    private fun observeDirectToMainEvent(mainViewModel: MainViewModel){
        mainViewModel.directToMainEvent.observe(viewLifecycleOwner, Observer {
            if (it == true) { // Observed state is true.
                Log.i("mainbody","is going to redirect to the connection fragment")
                mainViewModel.doneDirectToConnectionEvent()
                view?.findNavController()?.navigate(R.id.action_mainFragment_to_connectionFragment)
            }
        })
    }
}
