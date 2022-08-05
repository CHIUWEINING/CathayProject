package com.example.atry.presenter

import android.util.Log
import com.example.atry.Map.Companion.loading
import com.example.atry.atm.AtmItem
import com.example.atry.atm.AtmRequest
import com.example.atry.atm.AtmResponse
import com.example.atry.atm.toAtmItem
import com.example.atry.branch.*
import com.example.atry.contract.ContractMap
import com.example.atry.retrofit.Retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PresenterMap(private val view: ContractMap.IView2) : ContractMap.IPresenter2 {

    override fun getDataSimple(serviceSelect: String?,lng:Double,lat:Double) {
        loading=true
        if (serviceSelect == "BANK") {
            Retrofit.ApiBr.getData(BrRequest(25.038835000, 121.568656000, 5.0))
                .enqueue(object : Callback<List<branchResponse>?> {
                    override fun onResponse(
                        call: Call<List<branchResponse>?>,
                        response: Response<List<branchResponse>?>
                    ) {
                        var body: MutableList<BranchItem>? = null
                        response.body()?.let {
                            body = it.map {
                                it.tobranchItem()
                            }.toMutableList()
                        }
                        body?.let {
                            view.onSuccess(it as MutableList<Any>)
                        }
                    }

                    override fun onFailure(call: Call<List<branchResponse>?>, t: Throwable) {
                        println(t.message)
                        Log.d("MainActivity3", "onFailure${t.message}")
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
                        body?.let {
                            view.onSuccess(it as MutableList<Any>)
                        }
                    }

                    override fun onFailure(call: Call<List<AtmResponse>?>, t: Throwable) {
                        println(t.message)
                        Log.d("MainActivity3", "onFailure${t.message}")
                    }
                })
        }
    }

    override fun getDataBr(brRequest: BrRequest) {
        loading=true
        Retrofit.ApiBr.getData(brRequest).enqueue(object : Callback<List<branchResponse>?> {
            override fun onResponse(
                call: Call<List<branchResponse>?>,
                response: Response<List<branchResponse>?>
            ) {
                var body: MutableList<BranchItem>? = null
                response.body()?.let {
                    body = it.map {
                        it.tobranchItem()
                    }.toMutableList()
                }
                body?.let {
                    view.onSuccess(it as MutableList<Any>)
                }
            }

            override fun onFailure(call: Call<List<branchResponse>?>, t: Throwable) {
                println(t.message)
                Log.d("MainActivity3", "onFailure${t.message}")
            }
        })
    }

    override fun getDataAtm(atmRequest: AtmRequest) {
        loading=true
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
                    body?.let {
                        view.onSuccess(it as MutableList<Any>)
                    }
                }

                override fun onFailure(call: Call<List<AtmResponse>?>, t: Throwable) {
                    println(t.message)
                    Log.d("MainActivity3", "onFailure${t.message}")
                }
            })
    }

}