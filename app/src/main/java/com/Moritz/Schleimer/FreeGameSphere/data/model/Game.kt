package com.Moritz.Schleimer.FreeGameSphere.data.model

data class Game(
    val id: Int,
    val title: String?,
    val thumbnail: String?,
    val platform: String?,
    val genre: String?,
    val publisher: String?,
    val developer: String?,
    val release_date: String?,
    val description: String?,
    val game_url: String?,
    val isLiked: Boolean = false
)