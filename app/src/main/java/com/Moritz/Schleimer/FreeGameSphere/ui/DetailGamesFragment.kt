package com.Moritz.Schleimer.FreeGameSphere.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import coil.load
import com.Moritz.Schleimer.FreeGameSphere.MainViewModel
import com.Moritz.Schleimer.FreeGameSphere.R
import com.Moritz.Schleimer.FreeGameSphere.databinding.FragmentDetailGamesBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class DetailGamesFragment: Fragment() {

    private lateinit var binding: FragmentDetailGamesBinding
    private val viewModel: MainViewModel by activityViewModels()
    private val args: DetailGamesFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailGamesBinding.inflate(layoutInflater)
        viewModel.selectGameById(args.id)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.materialToolbar)
        var toolBarTitle= toolbar.findViewById<TextView>(R.id.toolbar_title)
        toolBarTitle.text = "Info über das Spiel"

        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.visibility = View.VISIBLE


        viewModel.selectedGame.observe(viewLifecycleOwner){
            it?.let { game ->
                binding.imageThumbnail.load(game.thumbnail)
                binding.tvTitleAPI.text = game.title
                binding.tvGenreAPI.text = game.genre
                binding.tvPlatformAPI.text = game.platform
                binding.tvPublisherAPI.text = game.publisher
                binding.tvDeveloperAPI.text = game.developer
                binding.tvReleaseDateAPI.text = game.release_date
                binding.tvShortDescriptionAPI.text = game.description ?: game.short_description
                binding.btnHomepage.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(game.game_url))
                    startActivity(intent)
                }
            }
        }

        viewModel.isFavorite.observe(viewLifecycleOwner){
            updateLikeButtonUi(it)
        }

        binding.imageView.setOnClickListener{
            viewModel.toggleFavorite()
        }
    }
    private fun updateLikeButtonUi(isFavorite:Boolean){
        if (isFavorite){
            binding.imageView.setImageResource(R.drawable.baseline_favorite_24)
        }else{
            binding.imageView.setImageResource(R.drawable.baseline_favorite_border_24)
        }
    }
}
