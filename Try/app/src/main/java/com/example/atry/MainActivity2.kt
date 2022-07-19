package com.example.atry


import com.example.atry.R
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.atry.databinding.ActivityMain2Binding
import com.example.atry.place.Place
import com.example.atry.place.PlaceRenderer
import com.example.atry.place.PlacesReader
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.maps.android.clustering.ClusterManager


class MainActivity2 : AppCompatActivity() {
    private lateinit var binding: ActivityMain2Binding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private val bicycleIcon: BitmapDescriptor by lazy {
        val color = ContextCompat.getColor(this, R.color.black)
        BitmapHelper.vectorToBitmap(this, R.drawable.list_icon, color)
    }
    private val places: List<Place> by lazy {
        PlacesReader(this).read()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment)as? SupportMapFragment
        mapFragment?.getMapAsync { googleMap ->
            //addMarkers(googleMap)
            addClusteredMarkers(googleMap)

            // Set custom info window adapter.
            // googleMap.setInfoWindowAdapter(MarkerInfoWindowAdapter(this))
            googleMap.setOnMapLoadedCallback {
                val bounds = LatLngBounds.builder()
                places.forEach { bounds.include(it.latLng) }
                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 20))
            }
        }


        var list= intent.getBundleExtra("list")?.getSerializable("list") as HashMap<String,String>

        fun expandCollapseSheet(){
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                binding.button.text = "Close Bottom Sheet"
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                binding.button.text = "Show Bottom Sheet"
            }
        }
        binding.button.setOnClickListener {
            expandCollapseSheet()
        }

        binding.more.background.alpha=0

        binding.more.setOnClickListener{
            showDialog()
        }

        binding.persistentBottomSheet.recyclerview.layoutManager= LinearLayoutManager(this)
        val data = ArrayList<ItemsViewModel>()

        for (i in 1..20) {
            data.add(ItemsViewModel( "Item " + i,"phone:"+i,"address:"+i))
        }

        // This will pass the ArrayList to our Adapter
        val adapter = CustomAdapter(data)

        // Setting the Adapter with the recyclerview
        binding.persistentBottomSheet.recyclerview.adapter = adapter
        adapter.setOnItemCLickListener(object:CustomAdapter.onItemClickListener{

            override fun onItemClick(item: ItemsViewModel) {
                Toast.makeText(this@MainActivity2,"You click number ${item.name}", Toast.LENGTH_SHORT).show()
                var passList= hashMapOf("name" to item.name
                    ,"addr" to item.addr
                    ,"phone" to item.phone)
                val box=Bundle()
                box.putSerializable("list",passList)
                val intent=Intent(this@MainActivity2,MainActivity3::class.java)
                intent.putExtra("list",box)
                startActivityForResult(intent,1)

            }
        })
        bottomSheetBehavior = BottomSheetBehavior.from( binding.persistentBottomSheet.persistentBottomSheet)
        bottomSheetBehavior.addBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback(){
            override fun onStateChanged(bottomSheet: View, state: Int) {
                print(state)
                when (state) {

                    BottomSheetBehavior.STATE_HIDDEN -> {
                         binding.button.text= "Show Bottom Sheet"
                    }
                    BottomSheetBehavior.STATE_EXPANDED ->
                        binding.button.text = "Close Bottom Sheet"
                    BottomSheetBehavior.STATE_COLLAPSED ->
                        binding.button.text = "Show Bottom Sheet"
                    BottomSheetBehavior.STATE_DRAGGING -> {
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                    }
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {

                    }
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        data?.extras?.let {
            if(requestCode==1 && resultCode== Activity.RESULT_OK){
                Toast.makeText(this@MainActivity2,it.getString("test"), Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun showDialog(){
        val filterDialog=FilterDialog(this)
        filterDialog
            .setConfirm(object:FilterDialog.IOnConfirmListener{
                override fun onConfirm(checkArray:Array<Boolean>) {

                    filterDialog.dismiss()
                }
            })
            .setCancel(object:FilterDialog.IOnCancelListener{
                override fun onCancel(dialog: FilterDialog?) {
                    filterDialog.dismiss()
                }
            }).show()

    }
    private fun addMarkers(googleMap: GoogleMap) {
        places.forEach { place ->
            val marker = googleMap.addMarker(
                MarkerOptions()
                    .title(place.name)
                    .position(place.latLng)
                    //.icon(bicycleIcon)
            )

            // Set place as the tag on the marker object so it can be referenced within
            // MarkerInfoWindowAdapter
            marker?.tag = place
        }
    }
    /**
     * Adds markers to the map with clustering support.
     */
    private fun addClusteredMarkers(googleMap: GoogleMap) {
        // Create the ClusterManager class and set the custom renderer.
        val clusterManager = ClusterManager<Place>(this, googleMap)
        clusterManager.renderer =
            PlaceRenderer(
                this,
                googleMap,
                clusterManager
            )

        // Set custom info window adapter
        clusterManager.markerCollection.setInfoWindowAdapter(MarkerInfoWindowAdapter(this))

        // Add the places to the ClusterManager.
        clusterManager.addItems(places)
        clusterManager.cluster()

        // Set ClusterManager as the OnCameraIdleListener so that it
        // can re-cluster when zooming in and out.
        googleMap.setOnCameraIdleListener {
            // When the camera stops moving, change the alpha value back to opaque.
            clusterManager.markerCollection.markers.forEach { it.alpha = 1.0f }
            clusterManager.clusterMarkerCollection.markers.forEach { it.alpha = 1.0f }

            // Call clusterManager.onCameraIdle() when the camera stops moving so that reclustering
            // can be performed when the camera stops moving.
            clusterManager.onCameraIdle()
        }
        googleMap.setOnCameraMoveStartedListener {
            clusterManager.markerCollection.markers.forEach { it.alpha = 0.3f }
            clusterManager.clusterMarkerCollection.markers.forEach { it.alpha = 0.3f }
        }
    }



}