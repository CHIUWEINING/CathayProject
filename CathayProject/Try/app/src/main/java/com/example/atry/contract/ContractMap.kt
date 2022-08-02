package com.example.atry.contract

import com.example.atry.atm.AtmItem
import com.example.atry.branch.branchItem

interface ContractMap {
    interface IPresenter2{
        fun getData(serviceSelect:String?)
    }
    interface IView2{
        fun onSuccessBr(responseBody:MutableList<branchItem>)
        fun onSuccessAtm(responseBody: MutableList<AtmItem>)
        fun onFail(message:String)
    }
}