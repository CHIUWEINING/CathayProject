package com.example.atry.contract

interface ContractAtm {
    interface IPresenterAtm{
        fun getCounty()
        fun getDistrict(county:String)
    }
    interface IViewAtm{
        fun adaptCounty(arrayCounty:Int)
        fun adaptDistrict(arrayDistrict:Int)
    }
}