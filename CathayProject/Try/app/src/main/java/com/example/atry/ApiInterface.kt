package com.example.atry

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiBranch{
    @GET("BM/findAllInformation/{center_lat}/{center_lng}/{km}")
    fun getData(@Path("center_lat")lat:Double,@Path("center_lng")lng:Double,@Path("km")km:Double):Call<List<branchResponse>>
}
interface ApiAtm{
    @GET("ATM/find/{center_lat}/{center_lng}/{km}")
    fun getData(@Path("center_lat")lat:Double,@Path("center_lng")lng:Double,@Path("km")km:Double):Call<List<AtmResponse>>
}