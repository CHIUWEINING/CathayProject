package com.example.atry.mapTool

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.example.atry.R
import com.example.atry.atm.AtmItem
import com.example.atry.branch.branchItem
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker


class MarkerInfoWindowAdapter(
    private val context: Context
,val type:Int) : GoogleMap.InfoWindowAdapter,GoogleMap.OnInfoWindowClickListener {
    private lateinit var name:String
    private lateinit var addr:String
    private lateinit var mListener: onWindowClickListener
    interface onWindowClickListener{
        fun onWindowClick(name:String)
    }
    fun setOnWindowClickListener(listener: onWindowClickListener){
        mListener=listener
    }
    override fun getInfoContents(marker: Marker): View? {
        // 1. Get tag
        if(type==1){
            val place= marker?.tag as? branchItem ?:return null
            name=place.name
            addr=place.address
        }else{
            val place= marker?.tag as? AtmItem ?:return null
            name=place.name
            addr=place.address
        }

        // 2. Inflate view and set title, address, and rating
        val view = LayoutInflater.from(context).inflate(
            R.layout.marker_info_contents, null
        )
        view.findViewById<TextView>(
            R.id.text_view_title
        ).text = name
        view.findViewById<TextView>(
            R.id.text_view_address
        ).text = addr
        /*view.setOnClickListener {
            println("hiiiiiii")
            mListener.onWindowClick(name,view)
        }*/
        return view
    }

    override fun getInfoWindow(marker: Marker): View? {
        // Return null to indicate that the
        // default window (white bubble) should be used
        return null
    }

    override fun onInfoWindowClick(p0: Marker) {
        mListener.onWindowClick(name)
    }

}