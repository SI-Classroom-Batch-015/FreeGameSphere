package com.Moritz.Schleimer.FreeGameSphere.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.Moritz.Schleimer.FreeGameSphere.MainViewModel
import com.Moritz.Schleimer.FreeGameSphere.adapter.GamesAdapter
import com.Moritz.Schleimer.FreeGameSphere.databinding.FragmentGamesBinding

class GamesFragment: Fragment() {

    private lateinit var binding: FragmentGamesBinding
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGamesBinding.inflate(layoutInflater)
        viewModel.loadGames()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvAdapter = GamesAdapter(emptyList())
        binding.rvGames.adapter = rvAdapter
        viewModel.games.observe(viewLifecycleOwner) {
            rvAdapter.submitListCall(it)
        }
    }
}