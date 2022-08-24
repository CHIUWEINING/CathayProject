package com.example.atry

import android.util.Log
import com.example.atry.atm.AtmItem
import com.example.atry.atm.AtmRequest
import com.example.atry.atm.AtmResponse
import com.example.atry.atm.toAtmItem
import com.example.atry.branch.BrRequest
import com.example.atry.branch.BranchItem
import com.example.atry.branch.BranchResponse
import com.example.atry.branch.tobranchItem
import com.example.atry.retrofit.Retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface IRepositoryMap{
    fun getDataBr(brRequest: BrRequest,listener:RepositoryMap.getDataCallback)
    fun getDataAtm(atmRequest: AtmRequest,listener:RepositoryMap.getDataCallback)
    fun getDataSimple(serviceSelect:String?,lng:Double,lat:Double,listener:RepositoryMap.getDataCallback)
}

class RepositoryMap :IRepositoryMap{
    interface getDataCallback{
        fun getDataResult(isGetDataSuccess:Boolean,body:MutableList<Any>?=null,message:String?=null)
    }
    override fun getDataBr(brRequest: BrRequest,listener:RepositoryMap.getDataCallback) {
        Map.loading =true
        Retrofit.ApiBr.getData(brRequest).enqueue(object : Callback<List<BranchResponse>?> {
            override fun onResponse(
                call: Call<List<BranchResponse>?>,
                response: Response<List<BranchResponse>?>
            ) {
                var body: MutableList<BranchItem>? = null
                response.body()?.let {
                    body = it.map {
                        it.tobranchItem()
                    }.toMutableList()
                }
                listener.getDataResult(true,body as MutableList<Any>)
            }

            override fun onFailure(call: Call<List<BranchResponse>?>, t: Throwable) {
                Log.d("MainActivity3", "onFailure${t.message}")
                listener.getDataResult(false, message = t.message)
            }
        })
    }

    override fun getDataAtm(atmRequest: AtmRequest,listener:RepositoryMap.getDataCallback) {
        Map.loading =true
        Retrofit.ApiAtm.getData(atmRequest)
            .enqueue(object : Callback<List<AtmResponse>?> {
                override fun onResponse(
                    call: Call<List<AtmResponse>?>,
                    response: Response<List<AtmResponse>?>
                ) {
                    var body: MutableList<AtmItem>? = null
                    response.body()?.let {
                        body = it.map {
                            it.toAtmItem()
                        }.toMutableList()
                    }
                    listener.getDataResult(true,body as MutableList<Any>)
                }

                override fun onFailure(call: Call<List<AtmResponse>?>, t: Throwable) {
                    println(t.message)
                    Log.d("MainActivity3", "onFailure${t.message}")
                    listener.getDataResult(false,message = t.message)
                }
            })
    }

    override fun getDataSimple(serviceSelect: String?, lng: Double, lat: Double,listener:RepositoryMap.getDataCallback) {
        Map.loading =true
        if (serviceSelect == "BANK") {
            Retrofit.ApiBr.getData(BrRequest(25.038835000, 121.568656000, 5.0))
                .enqueue(object : Callback<List<BranchResponse>?> {
                    override fun onResponse(
                        call: Call<List<BranchResponse>?>,
                        response: Response<List<BranchResponse>?>
                    ) {
                        var body: MutableList<BranchItem>? = null
                        response.body()?.let {
                            body = it.map {
                                it.tobranchItem()
                            }.toMutableList()
                        }
                        listener.getDataResult(true,body as MutableList<Any>)
                    }

                    override fun onFailure(call: Call<List<BranchResponse>?>, t: Throwable) {
                        println(t.message)
                        Log.d("MainActivity3", "onFailure${t.message}")
                        listener.getDataResult(false,message = t.message)
                    }
                })
        }else{
            Retrofit.ApiAtm.getData(AtmRequest(25.038835000, 121.568656000, 1.0))
                .enqueue(object : Callback<List<AtmResponse>?> {
                    override fun onResponse(
                        call: Call<List<AtmResponse>?>,
                        response: Response<List<AtmResponse>?>
                    ) {
                        var body: MutableList<AtmItem>? = null
                        response.body()?.let {
                            body = it.map {
                                it.toAtmItem()
                            }.toMutableList()
                        }
                        listener.getDataResult(true,body as MutableList<Any>)
                    }

                    override fun onFailure(call: Call<List<AtmResponse>?>, t: Throwable) {
                        println(t.message)
                        Log.d("MainActivity3", "onFailure${t.message}")
                        listener.getDataResult(false,message = t.message)
                    }
                })
        }
    }

}