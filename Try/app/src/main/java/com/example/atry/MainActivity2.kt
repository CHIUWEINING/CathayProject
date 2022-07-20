package com.example.atry


import com.example.atry.R
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity2 : AppCompatActivity() {
    private lateinit var binding: ActivityMain2Binding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private val BASE_URL="https://jsonplaceholder.typicode.com/"
    //"https://172.25.137.68:80/"
    //172.25.137.68
    //localhost:80/BM/find/25.038536533061507/121.56911953097298/0.2
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

        // Google map
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
        getMydata()
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


    private fun getMydata() {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(ApiInterface::class.java)

        val retrofitData= retrofitBuilder.getData()
        retrofitData.enqueue(object:Callback<List<MyDataItem>?>{
            override fun onResponse(
                call: Call<List<MyDataItem>?>,
                response: Response<List<MyDataItem>?>
            ) {
                val responseBody=response.body()!!
                val myAdapter=CustomAdapter(responseBody)
                binding.persistentBottomSheet.recyclerview.adapter=myAdapter
                myAdapter.setOnItemCLickListener(object:CustomAdapter.onItemClickListener{

                    override fun onItemClick(item: MyDataItem) {
                        Toast.makeText(this@MainActivity2,"You click number ${item.id}", Toast.LENGTH_SHORT).show()
                        var passList= hashMapOf("name" to item.body
                            ,"addr" to item.title
                            ,"phone" to item.id.toString())
                        val box=Bundle()
                        box.putSerializable("list",passList)
                        val intent=Intent(this@MainActivity2,MainActivity3::class.java)
                        intent.putExtra("list",box)
                        startActivityForResult(intent,1)

                    }
                })
            }

            override fun onFailure(call: Call<List<MyDataItem>?>, t: Throwable) {
                Log.d("MainActivity3","onFailure"+t.message)
            }

        })
    }
}