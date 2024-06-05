package com.Moritz.Schleimer.FreeGameSphere

import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.Moritz.Schleimer.FreeGameSphere.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHost = supportFragmentManager.findFragmentById(R.id.fragmentContainerView2) as NavHostFragment
        binding.bottomNavigationView.setupWithNavController(navHost.navController)

        val navController = navHost.navController

        binding.bottomNavigationView.setOnItemSelectedListener { item ->

            if (navController.currentDestination!!.id != item.itemId) {
                navController.navigate(item.itemId)
            }
            false
        }

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                binding.fragmentContainerView2.findNavController().navigateUp()
            }
        })
    }
}