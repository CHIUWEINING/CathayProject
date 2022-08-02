package com.example.atry.atm

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

data class AtmItem(
    val name: String,
    val sno:String,
    val latLng: LatLng,
    val address: String,
    val branchId: String?,
    val teleNo: String?,
    val kindname:String?,
    val qrCode:String?,
    val cardLess:String?,
    val iPass:String?,
    val visionImpaired:String?,
    val coin:String?,
    val face:String?,
    val koko:String?,
    val linePay:String?,
    val dist:Double?
): ClusterItem {
    override fun getPosition(): LatLng =
        latLng

    override fun getTitle(): String =
        name

    override fun getSnippet(): String =
        address
}
