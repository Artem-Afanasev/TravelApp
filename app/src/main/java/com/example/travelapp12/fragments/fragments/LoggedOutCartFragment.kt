package com.example.travelapp12.fragments.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.travelapp12.R
import com.example.travelapp12.databinding.FragmentCartAnloggedBinding

class LoggedOutCartFragment : Fragment(R.layout.fragment_cart_anlogged) {
    private lateinit var binding: FragmentCartAnloggedBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartAnloggedBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.authbtn.setOnClickListener {
            findNavController().navigate(R.id.action_loggedOutCartFragment_to_loginFragment)
        }
    }
}