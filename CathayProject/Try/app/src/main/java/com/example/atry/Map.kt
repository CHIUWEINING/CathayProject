package com.example.atry


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.atry.databinding.ActivityMapBinding
import com.example.atry.place.BranchRenderer
import com.example.atry.place.Place
import com.example.atry.place.PlaceRenderer
import com.example.atry.place.PlacesReader
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.maps.android.clustering.ClusterManager

//172.25.137.68
class Map : AppCompatActivity(),ContractMap.IView2 {
    private lateinit var binding: ActivityMapBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var presenter:ContractMap.IPresenter2
    private val BASE_URL="https://jsonplaceholder.typicode.com/"
    //"https://172.25.138.56:80/"
    //localhost:80/BM/find/25.038536533061507/121.56911953097298/0.2
    private val bicycleIcon: BitmapDescriptor by lazy {
        val color = ContextCompat.getColor(this, R.color.black)
        BitmapHelper.vectorToBitmap(this, R.drawable.list_icon, color)
    }
    private val places: List<Place> by lazy {
        PlacesReader(this).read()
    }
    private val myAdapter=CustomAdapter()
    private lateinit var ServiceSelect: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.title = "國泰服務站"
        binding.toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_24dp)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        binding.toolbar.inflateMenu(R.menu.menu_layout)
        binding.toolbar.setOnMenuItemClickListener{
            when (it.itemId) {
                R.id.filter -> showDialog()
            }
            false
        }

        //todo adapter init 先做
        binding.persistentBottomSheet.recyclerview.adapter=myAdapter
        myAdapter.setOnItemCLickListener(object:CustomAdapter.onItemClickListener{
            override fun onItemClick(item: branchItem) {
                var passList= hashMapOf("name" to item.name
                    ,"addr" to item.address
                    ,"phone" to item.teleNo.toString())
                val box=Bundle()
                box.putSerializable("list",passList)
                val intent=Intent(this@Map,MainActivity3::class.java)
                intent.putExtra("list",box)
                startActivityForResult(intent,1)
            }
        })
        presenter=PresenterMap(this)
        presenter.getData()




        binding.persistentBottomSheet.recyclerview.layoutManager= LinearLayoutManager(this)
        bottomSheetBehavior = BottomSheetBehavior.from( binding.persistentBottomSheet.persistentBottomSheet)
        bottomSheetBehavior.isFitToContents=false
        bottomSheetBehavior.halfExpandedRatio=0.6F
        bottomSheetBehavior.addBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback(){
            override fun onStateChanged(bottomSheet: View, state: Int) {
                when (state) {

                    BottomSheetBehavior.STATE_HIDDEN -> {

                    }
                    BottomSheetBehavior.STATE_EXPANDED ->{}

                    BottomSheetBehavior.STATE_COLLAPSED ->{}

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

    override fun onResume() {
        super.onResume()
        ServiceSelect= intent.getStringExtra("service").toString()
    }

    override fun onBackPressed() {
        if(bottomSheetBehavior.state!=BottomSheetBehavior.STATE_COLLAPSED){
            bottomSheetBehavior.state=BottomSheetBehavior.STATE_COLLAPSED
        }
        else super.onBackPressed()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        data?.extras?.let {
            if(requestCode==1 && resultCode== Activity.RESULT_OK){
                Toast.makeText(this@Map,it.getString("test"), Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun showDialog(){
        if(ServiceSelect!="null"){
            val filterDialog=FilterDialogAtm(this)
            filterDialog
                .setConfirm(object:FilterDialogAtm.IOnConfirmListener{
                    override fun onConfirm(checkArray:Array<Boolean>) {
                        //get the result of checkbox filter
                        filterDialog.dismiss()
                    }
                })
                .setCancel(object:FilterDialogAtm.IOnCancelListener{
                    override fun onCancel(dialog: FilterDialogAtm?) {
                        filterDialog.dismiss()
                    }
                }).show()
        }
        else{
            val filterDialog=FilterDialogBank(this)
            filterDialog
                .setConfirm(object :FilterDialogBank.IOnConfirmListener{
                    override fun onConfirm(checkArray:Array<Boolean>) {
                        //get the result of checkbox filter
                        filterDialog.dismiss()
                    }
                })
                .setCancel(object:FilterDialogBank.IOnCancelListener{
                    override fun onCancel(dialog: FilterDialogBank?) {
                        filterDialog.dismiss()
                    }
                }).show()
        }

    }
    /*private fun addMarkers(googleMap: GoogleMap) {
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
    }*/
    /**
     * Adds markers to the map with clustering support.
     */
    private fun addClusteredMarkers(googleMap: GoogleMap,responseBody: List<branchItem>) {
        // Create the ClusterManager class and set the custom renderer.
        val clusterManager = ClusterManager<branchItem>(this, googleMap)
        clusterManager.renderer =
            BranchRenderer(
                this,
                googleMap,
                clusterManager
            )

        // Set custom info window adapter
        clusterManager.markerCollection.setInfoWindowAdapter(MarkerInfoWindowAdapter(this))

        // Add the places to the ClusterManager.
        clusterManager.addItems(responseBody)
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

    override fun onSuccess(responseBody: List<branchItem>) {
        //val myAdapter=CustomAdapter(responseBody)
        myAdapter.mList=responseBody
        myAdapter.notifyDataSetChanged()
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment)as? SupportMapFragment
        mapFragment?.getMapAsync { googleMap ->
            //addMarkers(googleMap)
            addClusteredMarkers(googleMap,responseBody)

            // Set custom info window adapter.
            // googleMap.setInfoWindowAdapter(MarkerInfoWindowAdapter(this))
            googleMap.setOnMapLoadedCallback {
                val bounds = LatLngBounds.builder()
                //places.forEach { bounds.include(it.latLng) }
                responseBody?.forEach { bounds.include(it.latLng) }
                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 20))
            }
        }
    }

    override fun onFail(message: String) {
        Log.d("MainActivity3", "onFailure$message")
    }
    private fun expandCollapseSheet(){
        if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        } else {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        }
    }
}