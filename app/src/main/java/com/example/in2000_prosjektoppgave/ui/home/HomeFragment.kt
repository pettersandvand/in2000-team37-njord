package com.example.in2000_prosjektoppgave.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.in2000_prosjektoppgave.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint


/**
 * Home fragment: Initial screen for the application. Only displays text per now.
 */
@AndroidEntryPoint
class HomeFragment : Fragment() {

    // Binding for views
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}