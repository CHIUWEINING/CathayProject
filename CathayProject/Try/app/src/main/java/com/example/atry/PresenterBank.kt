package com.example.atry

class PresenterBank(private val view:ContractBank.IViewAtm):ContractBank.IPresenterAtm {
    override fun getCounty() {
        view.adaptCounty(R.array.County)
    }

    override fun getDistrict(county: String) {
        if(county=="Taipei"){
            view.adaptDistrict(R.array.Taipei)
        }else if(county=="New Taipei"){
            view.adaptDistrict(R.array.Ｎew_Taipei)
        }
    }
}