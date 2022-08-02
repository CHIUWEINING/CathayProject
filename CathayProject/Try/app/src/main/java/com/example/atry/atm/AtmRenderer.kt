package com.example.atry.atm

import android.content.Context
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.example.atry.mapTool.BitmapHelper
import com.example.atry.R
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer

/**
 * A custom cluster renderer for Place objects.
 */
class AtmRenderer(
    private val context: Context,
    map: GoogleMap,
    clusterManager: ClusterManager<AtmItem>
) : DefaultClusterRenderer<AtmItem>(context, map, clusterManager) {

    /**
     * The icon to use for each cluster item
     */
    private val atmIcon:BitmapDescriptor by lazy{
        val color = ContextCompat.getColor(context,
            R.color.black
        )
        BitmapHelper.vectorToBitmap(
            context,
            R.drawable.ic_atm_svgrepo_com,
            color
        )
    }

    /**
     * Method called before the cluster item (the marker) is rendered.
     * This is where marker options should be set.
     */
    override fun onBeforeClusterItemRendered(
        item: AtmItem,
        markerOptions: MarkerOptions
    ) {
        if (item.name != "您的位置") {
            markerOptions.title(item.name)
                .position(item.latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.atm_machine))
        }else {
            markerOptions.title(item.name)
                .position(item.latLng)
        }
    }

    /**
     * Method called right after the cluster item (the marker) is rendered.
     * This is where properties for the Marker object should be set.
     */
    override fun onClusterItemRendered(clusterItem: AtmItem, marker: Marker) {
        marker.tag = clusterItem
    }
}