package com.Moritz.Schleimer.FreeGameSphere.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.Moritz.Schleimer.FreeGameSphere.data.model.Game
import com.Moritz.Schleimer.FreeGameSphere.data.remote.BASE_URL
import com.Moritz.Schleimer.FreeGameSphere.databinding.FragmentGamesBinding
import com.Moritz.Schleimer.FreeGameSphere.databinding.ItemGameBinding

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
        holder.binding.tvTitel.text = game.title
        holder.binding.tvGenre.text = game.genre
        holder.binding.tvPlatform.text = game.platform
    }
    @SuppressLint("NotifyDataSetChanged")
    fun submitListCall(list: List<Game>) {
        dataset = list
        notifyDataSetChanged()
    }
}