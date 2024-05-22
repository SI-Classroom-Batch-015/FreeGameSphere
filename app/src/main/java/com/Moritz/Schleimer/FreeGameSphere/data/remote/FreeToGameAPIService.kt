package com.Moritz.Schleimer.FreeGameSphere.data.remote

import com.Moritz.Schleimer.FreeGameSphere.data.model.Game
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

const val  BASE_URL = "https://www.freetogame.com/api/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface FreeToGameAPIService {

    @GET("games")
    suspend fun getAllGames(): List<Game>

    @GET("game")
    suspend fun getGameById(@Query("id") id: Int): Game

}
object GameApi {
    val retrofitService: FreeToGameAPIService by lazy { retrofit.create(FreeToGameAPIService::class.java) }
}
