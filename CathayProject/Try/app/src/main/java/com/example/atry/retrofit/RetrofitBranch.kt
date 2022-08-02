package com.example.atry.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitBranch {
    private const val BASE_URL ="http://172.25.137.8:80/"
    private fun retrofitService(): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
    }
    val Api: ApiBranch by lazy{
        retrofitService().create(ApiBranch::class.java)
    }
}