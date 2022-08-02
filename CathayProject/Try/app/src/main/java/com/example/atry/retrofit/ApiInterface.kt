package com.example.atry.retrofit

import com.example.atry.atm.AtmRequest
import com.example.atry.atm.AtmResponse
import com.example.atry.branch.BrRequest
import com.example.atry.branch.branchResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiBranch{
    @GET("BM/findAllInformation/{center_lat}/{center_lng}/{km}")
    fun getData1(@Path("center_lat")lat:Double,@Path("center_lng")lng:Double,@Path("km")km:Double):Call<List<branchResponse>>
    @POST("BM/find")
    fun getData(@Body simpleRequest: BrRequest): Call<List<branchResponse>>
}
interface ApiAtm{
    @GET("ATM/find/{center_lat}/{center_lng}/{km}")
    fun getData1(@Path("center_lat")lat:Double,@Path("center_lng")lng:Double,@Path("km")km:Double):Call<List<AtmResponse>>
    @POST("ATM/find")
    fun getData(@Body simpleRequest: AtmRequest): Call<List<AtmResponse>>
}