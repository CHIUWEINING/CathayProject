package com.example.atry.place

import android.content.Context
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.example.atry.BitmapHelper
import com.example.atry.R
import com.example.atry.branchItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer

/**
 * A custom cluster renderer for Place objects.
 */
class BranchRenderer(
    private val context: Context,
    map: GoogleMap,
    clusterManager: ClusterManager<branchItem>
) : DefaultClusterRenderer<branchItem>(context, map, clusterManager) {

    /**
     * The icon to use for each cluster item
     */
    private val bicycleIcon: BitmapDescriptor by lazy {
        val color = ContextCompat.getColor(context,
            R.color.black
        )
        BitmapHelper.vectorToBitmap(
            context,
            R.drawable.ic_directions_bike_black_24dp,
            color
        )
    }
    private val bankIcon:BitmapDescriptor by lazy{
        val color = ContextCompat.getColor(context,
            R.color.black
        )
        BitmapHelper.vectorToBitmap(
            context,
            R.drawable.ic_baseline_account_balance_24,
            color
        )
    }

    /**
     * Method called before the cluster item (the marker) is rendered.
     * This is where marker options should be set.
     */
    override fun onBeforeClusterItemRendered(
        item: branchItem,
        markerOptions: MarkerOptions
    ) {
        markerOptions.title(item.name)
            .position(item.latLng)
            .icon(bankIcon)
    }

    /**
     * Method called right after the cluster item (the marker) is rendered.
     * This is where properties for the Marker object should be set.
     */
    override fun onClusterItemRendered(clusterItem: branchItem, marker: Marker) {
        marker.tag = clusterItem
    }
}