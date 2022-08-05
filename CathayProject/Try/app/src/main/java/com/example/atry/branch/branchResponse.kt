package com.example.atry.branch

import com.google.android.gms.maps.model.LatLng

data class branchResponse(
    val branchid: String,
    val branchname: String,
    val telno: String,
    val faxno: String,
    val address: String,
    val addressline1: String,
    val locality: String,
    val administrativearea: String,
    val fiscno: String,
    val zipcode : String,
    val corporate:String,
    val carloan:String,
    val branchtype:String,
    val valid:String,
    val tr_date:String,
    val tr_time:String,
    val longitude:String,
    val latitude:String,
    val enbranchname:String,
    val enaddress:String,
    val western:String,
    val isfx:String,
    val telno2:String,
    val faxno2:String,
    val email:String,
    val safety_box:String,
    val distanceToCenter:Double
){
    data class Geometry(
        val location: GeometryLocation
    )

    data class GeometryLocation(
        val lat: Double,
        val lng: Double
    )
}
fun branchResponse.tobranchItem(): BranchItem = BranchItem(
    name = branchname,
    latLng = LatLng(latitude.toDouble(), longitude.toDouble()),
    branchId= branchid,
    address = address,
    zipCode = zipcode,
    faxNo = faxno,
    teleNo = telno,
    dist = distanceToCenter,
    safetyBox = safety_box,
    isfx = isfx
)