package com.example.atry

import retrofit2.Call
import retrofit2.Response

interface ContractMap {
    interface IPresenter2{
        fun getData(serviceSelect:String?)
    }
    interface IView2{
        fun onSuccessBr(responseBody:List<branchItem>)
        fun onSuccessAtm(responseBody: List<AtmItem>)
        fun onFail(message:String)
    }
}