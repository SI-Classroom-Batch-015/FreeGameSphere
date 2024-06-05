package com.Moritz.Schleimer.FreeGameSphere.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.Moritz.Schleimer.FreeGameSphere.MainViewModel
import com.Moritz.Schleimer.FreeGameSphere.R
import com.Moritz.Schleimer.FreeGameSphere.STATES
import com.Moritz.Schleimer.FreeGameSphere.databinding.FragmentRegisterBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class RegisterFragment: Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.materialToolbar)
        var toolBarTitle = toolbar.findViewById<TextView>(R.id.toolbar_title)
        toolBarTitle.text = ""

        viewModel.error.observe(viewLifecycleOwner) {
            it?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }

        val bottomNav =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.visibility = View.GONE

        binding.btnRegister2.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val passwort = binding.passwordEt.text.toString()
            val confirmPassword = binding.passwortSafeEt.text.toString()
            viewModel.signUp(email, passwort,confirmPassword)

        }
        viewModel.currentState.observe(viewLifecycleOwner) {
            when (it) {
                STATES.LOADING -> {
                }

                STATES.SUCCESS -> {
                    Toast.makeText(requireContext(), "Registered Successfully", Toast.LENGTH_SHORT)
                        .show()
                    findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
                    viewModel.clearState()
                }

                STATES.ERROR -> {
                }

                else -> {}
            }
        }
    }
}