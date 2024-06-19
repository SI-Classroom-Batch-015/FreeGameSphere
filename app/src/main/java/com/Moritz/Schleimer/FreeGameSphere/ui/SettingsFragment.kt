package com.Moritz.Schleimer.FreeGameSphere.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.CircleCropTransformation
import com.Moritz.Schleimer.FreeGameSphere.MainViewModel
import com.Moritz.Schleimer.FreeGameSphere.R
import com.Moritz.Schleimer.FreeGameSphere.databinding.FragmentSettingsBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File

class SettingsFragment:Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private val viewModel: MainViewModel by activityViewModels()
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { processImageUri(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(layoutInflater)
        viewModel.loadUserProfile()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.materialToolbar)
        var toolBarTitle= toolbar.findViewById<TextView>(R.id.toolbar_title)
        toolBarTitle.text = "Einstellungen"

        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.visibility = View.VISIBLE


        binding.btnLogout.setOnClickListener {
            viewModel.signOut()
            it.findNavController().navigate(R.id.loginFragment)
            viewModel.clearState()
        }

        binding.ivProfilePic.setOnClickListener{
            pickImageLauncher.launch("image/*")
        }

        viewModel.profilePhotoUrl.observe(viewLifecycleOwner){uri ->
            uri?.let {
                binding.ivProfilePic.load(it){
                    transformations(CircleCropTransformation())
                    error(R.drawable.ic_launcher_foreground)
                }
            }
        }

        binding.btnSafe.setOnClickListener {
            val image = viewModel.profilePhotoUrl.value
            viewModel.updateProfile(image.toString())
            Toast.makeText(requireContext(),"Bild erfolgreich geändert!", Toast.LENGTH_SHORT).show()
        }

        viewModel.currentProfile.observe(viewLifecycleOwner){
            binding.ivProfilePic.load(it?.imageUrl){
                transformations(CircleCropTransformation())
                error(R.drawable.ic_launcher_foreground)
            }
        }
        binding.btnChangePassword.setOnClickListener {
            val action = SettingsFragmentDirections.actionSettingsFragmentToChangePasswordFragment()
            findNavController().navigate(action)
        }
    }
    private fun processImageUri(uri: Uri) {
        val file = File(requireContext().cacheDir, "profile_photo.jpg")
        file.outputStream().use { outputStream ->
            requireContext().contentResolver.openInputStream(uri)?.copyTo(outputStream)
        }
        viewModel.uploadProfilePhoto(file)
    }
}