package com.example.atry

import com.google.android.gms.maps.model.LatLng

data class AtmResponse(
    val sno: String,
    val branchid:String,
    val branchname:String,
    val setaddresskind:String,
    val maker:String,
    val inout:String,
    val mtype:String,
    val kindname:String,
    val address :String,
    val mno: String,
    val area:String,
    val tel:String,
    val kindcode:String,
    val servicecode:String,
    val ic:String,
    val longitude:Double,
    val latitude:Double,
    val newservicecode:String,
    val koko:String,
    val linepay:String,
    val enSetaddresskind:String,
    val enKindname:String,
    val enAddress:String,
    val ipass:String,
    val visionimpaired:String,
    val cardless:String,
    val qrcode:String,
    val coin:String,
    val face:String
)
fun AtmResponse.toAtmItem():AtmItem=AtmItem(
    name=branchname,
    latLng = LatLng(latitude, longitude),
    branchId = branchid,
    address= address,
    teleNo = tel,
    kindname = kindname,
    inOut = inout
)
