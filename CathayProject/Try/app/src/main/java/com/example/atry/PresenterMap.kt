package com.example.atry

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PresenterMap(private val view: ContractMap.IView2) : ContractMap.IPresenter2
{
    override fun getData() {
        RetrofitBranch.Api.getData(25.038536533061507,121.56911953097298,5.0).enqueue(object : Callback<List<branchResponse>?> {
            override fun onResponse(
                call: Call<List<branchResponse>?>,
                response: Response<List<branchResponse>?>
            ) {
                var body:List<branchItem>?=null
                response.body()?.let{
                    body=it.map{
                        it.tobranchItem()
                    }
                }
                body?.let {
                    view.onSuccess(it)
                }
            }
            override fun onFailure(call: Call<List<branchResponse>?>, t: Throwable) {
                println(t.message)
                Log.d("MainActivity3", "onFailure${t.message}")
            }
        })
    }

}