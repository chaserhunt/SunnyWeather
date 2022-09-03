package com.example.sunnyweather.logic.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceCreator {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.caiyunapp.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun  <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    inline fun <reified T> create(): T = create(T::class.java)
}