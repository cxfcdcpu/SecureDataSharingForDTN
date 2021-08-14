package com.example.securedatasharingfordtn.mainbody

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.securedatasharingfordtn.R
import com.example.securedatasharingfordtn.SharedViewModel
import com.example.securedatasharingfordtn.login.LoginViewModel
import it.unisa.dia.gas.jpbc.*
import it.unisa.dia.gas.plaf.jpbc.pairing.*
import com.example.securedatasharingfordtn.login.*
import java.io.InputStream
class MainFragment : Fragment(R.layout.fragment_main){

    val sharedModel=ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

    var pair : Pairing = PairingFactory.getPairing("a.properties");
}
