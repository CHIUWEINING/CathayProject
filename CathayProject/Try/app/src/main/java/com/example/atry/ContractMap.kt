package com.example.atry

import retrofit2.Call
import retrofit2.Response

interface ContractMap {
    interface IPresenter2{
        fun getData()
    }
    interface IView2{
        fun onSuccess(responseBody:List<branchItem>)
        fun onFail(message:String)
    }
}