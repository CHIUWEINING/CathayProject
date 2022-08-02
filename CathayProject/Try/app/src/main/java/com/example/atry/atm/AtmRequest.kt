package com.example.atry.atm

data class AtmRequest(
    val center_lat:Double,
    val center_lng:Double,
    val km:Double,
    val koko:String?="0",
    val linepay:String?="0",
    val ipass:String?="0",
    val visionimpaired:String?="0",
    val cardless:String?="0",
    val qrcode:String?="0",
    val coin:String?="0",
    val face:String?="0"
    )
