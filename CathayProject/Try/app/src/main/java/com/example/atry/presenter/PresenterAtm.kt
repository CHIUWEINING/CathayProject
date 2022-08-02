package com.example.atry.presenter


import com.example.atry.R
import com.google.gson.Gson
import com.example.atry.contract.ContractAtm


class PresenterAtm(private val view: ContractAtm.IViewAtm): ContractAtm.IPresenterAtm {

   /* private val inputStream: InputStream
        get() = context.resources.openRawResource(R.raw.counties)*/

    override fun getCounty() {
       /* val itemType = object : TypeToken<List<DataItem1>>() {}.type
        val reader = InputStreamReader(inputStream)
        var array:ArrayList<DataItem1>?=null

        array=gson.fromJson<ArrayList<DataItem1>>(reader, itemType)
        array.forEachIndexed { index, dataItem1 ->println("$dataItem1")  }*/
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