package com.patilmanas87.pokedexai.retrofit

import com.patilmanas87.pokedexai.model.PathReuest
import com.patilmanas87.pokedexai.model.SendRequest
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface FakeYouApi {
    @POST("/tts/inference")
    suspend fun getVoiceInference(@retrofit2.http.Body requestBody: com.patilmanas87.pokedexai.model.Body?):SendRequest

    @GET("/tts/job/{id}")
    suspend fun getPath(@Path("id") variable: String): PathReuest

}