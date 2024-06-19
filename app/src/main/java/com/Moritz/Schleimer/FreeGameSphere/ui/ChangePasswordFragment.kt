package com.Moritz.Schleimer.FreeGameSphere.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.Moritz.Schleimer.FreeGameSphere.MainViewModel
import com.Moritz.Schleimer.FreeGameSphere.databinding.FragmentChangePasswordBinding

class ChangePasswordFragment: Fragment(){
    private lateinit var binding: FragmentChangePasswordBinding
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChangePasswordBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSafe2.setOnClickListener {
            val currentPassword = binding.tietCurrentPassword.text.toString()
            val newPassword = binding.tietNewPasswort.text.toString()
            val confirmPassword = binding.tietConfirmPassword.text.toString()
            if (newPassword != confirmPassword){
                Toast.makeText(requireContext(),"Passwörter stimmen nicht überein!",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(
                    requireContext(),
                    "Passwort erfolgreich geändert!",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.changePassword(currentPassword,newPassword)
                findNavController().navigate(ChangePasswordFragmentDirections.actionChangePasswordFragmentToSettingsFragment())
            }
        }
    }
}