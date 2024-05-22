package com.Moritz.Schleimer.FreeGameSphere.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.Moritz.Schleimer.FreeGameSphere.data.model.Game
import com.Moritz.Schleimer.FreeGameSphere.data.remote.BASE_URL
import com.Moritz.Schleimer.FreeGameSphere.databinding.FragmentGamesBinding
import com.Moritz.Schleimer.FreeGameSphere.databinding.ItemGameBinding
import com.Moritz.Schleimer.FreeGameSphere.ui.GamesFragmentDirections

class GamesAdapter(
    private var dataset: List<Game>
): RecyclerView.Adapter<GamesAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(val binding: ItemGameBinding) :
            RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemGameBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val game = dataset[position]
        holder.binding.ivGame.load(game.thumbnail)
        holder.binding.tvGenre.text = game.genre
        holder.binding.tvPlattform.text = game.platform
        holder.binding.cvGame.setOnClickListener {
            val action = GamesFragmentDirections.actionGamesFragment2ToDetailGamesFragment(game.id)
            it.findNavController().navigate(action)
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    fun submitListCall(list: List<Game>) {
        dataset = list
        notifyDataSetChanged()
    }
}