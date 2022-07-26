package com.example.atry

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

data class branchItem(
    val name:String,
    val latLng:LatLng,
    val address:String,
    val branchId:String?,
    val zipCode:String?,
    val faxNo:String?,
    val teleNo:String?
    ):ClusterItem{
    override fun getPosition(): LatLng =
        latLng

    override fun getTitle(): String =
        name

    override fun getSnippet(): String =
        address
}
