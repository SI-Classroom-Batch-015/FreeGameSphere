package com.Moritz.Schleimer.FreeGameSphere.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.Moritz.Schleimer.FreeGameSphere.MainViewModel
import com.Moritz.Schleimer.FreeGameSphere.R
import com.Moritz.Schleimer.FreeGameSphere.adapter.GamesAdapter
import com.Moritz.Schleimer.FreeGameSphere.databinding.FragmentFavoritesBinding
import com.Moritz.Schleimer.FreeGameSphere.databinding.FragmentGamesBinding
import com.Moritz.Schleimer.FreeGameSphere.databinding.ItemGameBinding
import com.google.android.material.appbar.MaterialToolbar

class FavoritesFragment: Fragment() {

    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var binding2: ItemGameBinding
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(layoutInflater)
        viewModel.loadFavorites()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.materialToolbar)
        var toolBarTitle= toolbar.findViewById<TextView>(R.id.toolbar_title)
        toolBarTitle.text = "Favoriten"


        viewModel.favoriteGames.observe(viewLifecycleOwner){
            binding.rvFavorites.adapter = GamesAdapter(it)
        }
        binding2.cvGame.setOnClickListener {

        }

    }
}