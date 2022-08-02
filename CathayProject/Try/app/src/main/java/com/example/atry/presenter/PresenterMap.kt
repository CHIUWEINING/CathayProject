package com.example.atry.presenter

import android.util.Log
import com.example.atry.Map.Companion.loading
import com.example.atry.retrofit.RetrofitAtm
import com.example.atry.retrofit.RetrofitBranch
import com.example.atry.atm.AtmItem
import com.example.atry.atm.AtmRequest
import com.example.atry.atm.AtmResponse
import com.example.atry.atm.toAtmItem
import com.example.atry.branch.*
import com.example.atry.contract.ContractMap
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PresenterMap(private val view: ContractMap.IView2) : ContractMap.IPresenter2 {

    override fun getDataSimple(serviceSelect: String?,lng:Double,lat:Double) {
        loading=true
        if (serviceSelect == "BANK") {
            RetrofitBranch.Api.getData(BrRequest(lat, lng, 5.0))
                .enqueue(object : Callback<List<branchResponse>?> {
                    override fun onResponse(
                        call: Call<List<branchResponse>?>,
                        response: Response<List<branchResponse>?>
                    ) {
                        var body: MutableList<branchItem>? = null
                        response.body()?.let {
                            body = it.map {
                                it.tobranchItem()
                            }.toMutableList()
                        }
                        body?.let {
                            view.onSuccessBr(it)
                        }
                    }

                    override fun onFailure(call: Call<List<branchResponse>?>, t: Throwable) {
                        println(t.message)
                        Log.d("MainActivity3", "onFailure${t.message}")
                    }
                })
        }else{
            RetrofitAtm.Api.getData(AtmRequest(lat, lng, 1.0))
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
                            view.onSuccessAtm(it)
                        }
                    }

                    override fun onFailure(call: Call<List<AtmResponse>?>, t: Throwable) {
                        println(t.message)
                        Log.d("MainActivity3", "onFailure${t.message}")
                    }
                })
        }
    }

}