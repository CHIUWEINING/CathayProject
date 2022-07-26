package com.example.atry

interface ContractBank {
    interface IPresenterAtm{
        fun getCounty()
        fun getDistrict(county:String)
    }
    interface IViewAtm{
        fun adaptCounty(arrayCounty:Int)
        fun adaptDistrict(arrayDistrict:Int)
    }
}