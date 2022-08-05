package com.example.atry.contract

import com.example.atry.atm.AtmItem
import com.example.atry.atm.AtmRequest
import com.example.atry.branch.BrRequest
import com.example.atry.branch.branchItem

interface ContractMap {
    interface IPresenter2{
        fun getDataSimple(serviceSelect:String?,lng:Double,lat:Double)
        fun getDataBr(brRequest: BrRequest)
        fun getDataAtm(atmRequest: AtmRequest)
    }
    interface IView2{
        //fun onSuccessBr(responseBody:MutableList<branchItem>)
        fun onSuccess(responseBody: MutableList<Any>)
        fun onFail(message:String)
    }
}