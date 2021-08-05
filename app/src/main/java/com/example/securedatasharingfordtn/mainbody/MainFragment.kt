package com.example.securedatasharingfordtn.mainbody

import androidx.fragment.app.Fragment
import com.example.securedatasharingfordtn.R
import it.unisa.dia.gas.jpbc.*
import it.unisa.dia.gas.plaf.jpbc.pairing.*
import java.io.InputStream


class MainFragment : Fragment(R.layout.fragment_main){


    var pair : Pairing = PairingFactory.getPairing("a.properties");
}