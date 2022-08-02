package com.example.atry.branch

data class BrRequest(val center_lat:Double, val center_lng:Double, val km:Double,val safety_box:String?="",val isfx:String?="")
