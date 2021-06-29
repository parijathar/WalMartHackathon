package com.example.fiapp.network

import com.example.fiapp.model.ApodNasaReponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkApi {

    @GET("planetary/apod")
    fun getApodNASA(@Query("api_key") api: String): Call<ApodNasaReponse?>?
}