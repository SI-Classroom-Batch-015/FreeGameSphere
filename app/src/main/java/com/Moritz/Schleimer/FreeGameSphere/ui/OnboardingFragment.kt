package com.Moritz.Schleimer.FreeGameSphere.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.Moritz.Schleimer.FreeGameSphere.MainViewModel
import com.Moritz.Schleimer.FreeGameSphere.R
import com.Moritz.Schleimer.FreeGameSphere.databinding.FragmentGamesBinding
import com.Moritz.Schleimer.FreeGameSphere.databinding.FragmentOnboardingBinding
import com.google.android.material.appbar.MaterialToolbar

class OnboardingFragment: Fragment() {

    private lateinit var binding: FragmentOnboardingBinding
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnboardingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.materialToolbar)
        var toolBarTitle= toolbar.findViewById<TextView>(R.id.toolbar_title)
        toolBarTitle.text = ""

        binding.btnGetStartet.setOnClickListener {
            val action = OnboardingFragmentDirections.actionOnboardingFragmentToLoginFragment()
            findNavController().navigate(action)
        }
    }
}