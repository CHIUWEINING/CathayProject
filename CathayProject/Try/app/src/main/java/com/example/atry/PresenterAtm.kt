package com.example.atry

import com.example.atry.place.Place
import com.example.atry.place.PlaceResponse
import com.example.atry.place.toPlace
import com.google.gson.Gson
import java.io.InputStream
import java.io.InputStreamReader
import android.content.Context
import com.google.gson.reflect.TypeToken


class PresenterAtm(private val view:ContractAtm.IViewAtm):ContractAtm.IPresenterAtm {
    private val gson = Gson()

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