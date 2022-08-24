package com.example.atry.presenter

import android.util.Log
import com.example.atry.Map.Companion.loading
import com.example.atry.RepositoryMap
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
    val repositoryMap=RepositoryMap()
    val callbackFunc=object:RepositoryMap.getDataCallback{
        override fun getDataResult(isGetDataSuccess: Boolean, body: MutableList<Any>?,message:String?) {
            if(isGetDataSuccess){
                view.onSuccess(body!!)
            }else{
                view.onFail(message!!)
            }
        }

    }
    override fun getDataSimple(serviceSelect: String?,lng:Double,lat:Double) {
        repositoryMap.getDataSimple(serviceSelect,lng,lat,callbackFunc)
    }

    override fun getDataBr(brRequest: BrRequest) {
        repositoryMap.getDataBr(brRequest,callbackFunc)
    }

    override fun getDataAtm(atmRequest: AtmRequest) {
        repositoryMap.getDataAtm(atmRequest,callbackFunc)
    }

}