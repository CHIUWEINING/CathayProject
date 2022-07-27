package com.example.atry

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitAtm {
    private const val BASE_URL ="http://172.25.138.56:80/"
    private fun retrofitService(): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
    }
    val Api:ApiAtm by lazy{
        retrofitService().create(ApiAtm::class.java)
    }
}