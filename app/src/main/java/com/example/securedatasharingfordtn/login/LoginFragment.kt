package com.example.securedatasharingfordtn.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.securedatasharingfordtn.R
import androidx.databinding.DataBindingUtil
import com.example.securedatasharingfordtn.databinding.FragmentLoginBinding

class LoginFragment : Fragment()  {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentLoginBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_login,container,false
        )


        return binding.root
    }
}