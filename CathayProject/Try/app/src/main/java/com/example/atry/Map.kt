package com.example.atry


import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.atry.recycleview.AtmAdapter
import com.example.atry.atm.AtmItem
import com.example.atry.recycleview.BranchAdapter
import com.example.atry.branch.branchItem
import com.example.atry.contract.ContractMap
import com.example.atry.databinding.ActivityMapBinding
import com.example.atry.filterDialog.FilterDialogAtm
import com.example.atry.filterDialog.FilterDialogBank
import com.example.atry.mapTool.BitmapHelper
import com.example.atry.mapTool.MarkerInfoWindowAdapter
import com.example.atry.presenter.PresenterMap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory

//172.25.137.68
class Map : AppCompatActivity(), ContractMap.IView2 {

    companion object {
        const val name_const = "name"
        const val phone_const = "phone"
        const val addr_const = "addr"
        var loading = true
    }

    private lateinit var binding: ActivityMapBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var presenter: ContractMap.IPresenter2

    //"https://172.25.138.56:80/"
    //localhost:80/BM/find/25.038536533061507/121.56911953097298/0.2
    private val bankIcon: BitmapDescriptor by lazy {
        val color = ContextCompat.getColor(
            this,
            R.color.black
        )
        BitmapHelper.vectorToBitmap(
            this,
            R.drawable.ic_baseline_account_balance_24,
            color
        )
    }
    private val branchAdapter = BranchAdapter()
    private val atmAdapter = AtmAdapter()
    private lateinit var ServiceSelect: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        setExitSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementsUseOverlay = true
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.title = "國泰服務站"
        binding.toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_24dp)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        binding.toolbar.inflateMenu(R.menu.menu_layout)
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.filter -> showDialog()
            }
            false
        }
        branchAdapter.setOnItemCLickListener(object : BranchAdapter.onItemClickListener {
            override fun onItemClick(item: branchItem) {
                var passList = hashMapOf(
                    "type" to "br",
                    "name" to item.name, "addr" to item.address, "phone" to item.teleNo.toString()
                )
                val box = Bundle()
                box.putSerializable("list", passList)
                val intent = Intent(this@Map, MainActivity3::class.java)
                intent.putExtra("list", box)
                startActivityForResult(intent, 1)
            }
        })
        atmAdapter.setOnItemCLickListener(object : AtmAdapter.onItemClickListener {
            override fun onItemClick(item: AtmItem, view: View, position: Int) {
                var passList = hashMapOf(
                    "type" to "atm",
                    "name" to item.name, "addr" to item.address, "kindname" to item.kindname
                )
                val box = Bundle()
                box.putSerializable("list", passList)
                val intent = Intent(this@Map, MainActivity3::class.java)
                intent.putExtra("list", box)
                val nameView = view.findViewById<View>(R.id.name)
                val phoneView = view.findViewById<View>(R.id.phone)
                val addrView = view.findViewById<View>(R.id.addr)
                /*val activityOptions=ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this@Map,
                    Pair(nameView, name_const),
                    Pair(phoneView, phone_const),
                    Pair(addrView, addr_const)
                    )*/
                view.transitionName = "share_element_container"
                val options = ActivityOptions.makeSceneTransitionAnimation(
                    this@Map,
                    view,
                    "share_element_container"
                )
                startActivityForResult(intent, 1, options.toBundle())
            }
        })
        presenter = PresenterMap(this)
        binding.persistentBottomSheet.recyclerview.layoutManager = LinearLayoutManager(this)
        bottomSheetBehavior =
            BottomSheetBehavior.from(binding.persistentBottomSheet.persistentBottomSheet)
        bottomSheetBehavior.isFitToContents = false
        bottomSheetBehavior.halfExpandedRatio = 0.4F
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, state: Int) {
                when (state) {

                    BottomSheetBehavior.STATE_HIDDEN -> {

                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {}

                    BottomSheetBehavior.STATE_COLLAPSED -> {}

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
        ServiceSelect = intent.getStringExtra("service").toString()
        //todo adapter init 先做
        presenter.getData(ServiceSelect)
        if (ServiceSelect == "null") binding.persistentBottomSheet.recyclerview.adapter =
            branchAdapter
        else binding.persistentBottomSheet.recyclerview.adapter = atmAdapter
    }

    override fun onBackPressed() {
        if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        } else super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        data?.extras?.let {
            if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
                Toast.makeText(this@Map, it.getString("test"), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDialog() {
        if (ServiceSelect != "null") {
            val filterDialog = FilterDialogAtm(this)
            filterDialog
                .setConfirm(object : FilterDialogAtm.IOnConfirmListener {
                    override fun onConfirm(checkArray: Array<Boolean>) {
                        //get the result of checkbox filter
                        filterDialog.dismiss()
                    }
                })
                .setCancel(object : FilterDialogAtm.IOnCancelListener {
                    override fun onCancel(dialog: FilterDialogAtm?) {
                        filterDialog.dismiss()
                    }
                }).show()
        } else {
            val filterDialog = FilterDialogBank(this)
            filterDialog
                .setConfirm(object : FilterDialogBank.IOnConfirmListener {
                    override fun onConfirm(checkArray: Array<Boolean>) {
                        //get the result of checkbox filter
                        filterDialog.dismiss()
                    }
                })
                .setCancel(object : FilterDialogBank.IOnCancelListener {
                    override fun onCancel(dialog: FilterDialogBank?) {
                        filterDialog.dismiss()
                    }
                }).show()
        }

    }

    private fun addMarkersBr(googleMap: GoogleMap, responseBody: MutableList<branchItem>) {
        val markerInfoWindowAdapter = MarkerInfoWindowAdapter(this, 1)
        markerInfoWindowAdapter.setOnWindowClickListener(object :
            MarkerInfoWindowAdapter.onWindowClickListener {
            override fun onWindowClick(name: String) {
                var layoutManager =
                    binding.persistentBottomSheet.recyclerview.layoutManager as LinearLayoutManager
                branchAdapter.mList?.forEachIndexed { id, item ->
                    if (item.name == name) {
                        layoutManager.scrollToPositionWithOffset(id, 0)
                    }
                }
            }
        })
        googleMap.setInfoWindowAdapter(markerInfoWindowAdapter)
        responseBody.forEach { item ->
            if (item.name == "您的位置") {
                val marker = googleMap.addMarker(
                    MarkerOptions()
                        .title(item.name)
                        .position(item.latLng)
                )
                // Set place as the tag on the marker object so it can be referenced within
                // MarkerInfoWindowAdapter
                marker?.tag = item
            } else {
                val marker = googleMap.addMarker(
                    MarkerOptions()
                        .title(item.name)
                        .position(item.latLng)
                        .icon(bankIcon)
                )
                // Set place as the tag on the marker object so it can be referenced within
                // MarkerInfoWindowAdapter
                marker?.tag = item
            }
        }
    }

    private fun addMarkersAtm(googleMap: GoogleMap, responseBody: MutableList<AtmItem>) {
        val markerInfoWindowAdapter = MarkerInfoWindowAdapter(this, 2)
        markerInfoWindowAdapter.setOnWindowClickListener(object :
            MarkerInfoWindowAdapter.onWindowClickListener {
            override fun onWindowClick(name: String) {
                println("hello")
                var layoutManager =
                    binding.persistentBottomSheet.recyclerview.layoutManager as LinearLayoutManager
                atmAdapter.mList?.forEachIndexed { id, item ->
                    if (item.name == name) {
                        layoutManager.scrollToPositionWithOffset(id, 0)
                    }
                }
            }
        })
        googleMap.setInfoWindowAdapter(markerInfoWindowAdapter)
        responseBody.forEach { item ->
            if (item.name == "您的位置") {
                val marker = googleMap.addMarker(
                    MarkerOptions()
                        .title(item.name)
                        .position(item.latLng)
                )
                // Set place as the tag on the marker object so it can be referenced within
                // MarkerInfoWindowAdapter
                marker?.tag = item
            } else {
                val marker = googleMap.addMarker(
                    MarkerOptions()
                        .title(item.name)
                        .position(item.latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.atm_machine))
                )
                // Set place as the tag on the marker object so it can be referenced within
                // MarkerInfoWindowAdapter
                marker?.tag = item
            }
        }
    }

    /**
     * Adds markers to the map with clustering support.
     */


    override fun onSuccessBr(responseBody: MutableList<branchItem>) {
        //val myAdapter=CustomAdapter(responseBody)
        branchAdapter.mList = responseBody
        val now =
            branchItem("您的位置", LatLng(25.038536533061507, 121.56911953097298), "", "", "", "", "")
        branchAdapter.mList?.let {
            it.add(now)
        }
        branchAdapter.notifyDataSetChanged()
        (supportFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment)?.run {
            getMapAsync { googleMap ->
                //addMarkers(googleMap)
                googleMap.setInfoWindowAdapter(MarkerInfoWindowAdapter(this@Map, 1))
                addMarkersBr(googleMap, responseBody)

                // Set custom info window adapter.
                // googleMap.setInfoWindowAdapter(MarkerInfoWindowAdapter(this))
                googleMap.setOnMapLoadedCallback {
                    val bounds = LatLngBounds.builder()
                    //places.forEach { bounds.include(it.latLng) }
                    responseBody.forEach { if (it.name == "您的位置") bounds.include(it.latLng) }

                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 20))
                }
            }
        }

    }

    override fun onSuccessAtm(responseBody: MutableList<AtmItem>) {
        atmAdapter.mList = responseBody
        val now =
            AtmItem("您的位置", LatLng(25.038536533061507, 121.56911953097298), "", "", "", "", "")
        atmAdapter.mList?.let {
            it.add(now)
        }
        atmAdapter.notifyDataSetChanged()
        (supportFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment)?.run {
            getMapAsync { googleMap ->
                //addMarkers(googleMap)
                googleMap.setInfoWindowAdapter(MarkerInfoWindowAdapter(this@Map, 1))
                addMarkersAtm(googleMap, responseBody)

                // Set custom info window adapter.
                // googleMap.setInfoWindowAdapter(MarkerInfoWindowAdapter(this))
                googleMap.setOnMapLoadedCallback {
                    val bounds = LatLngBounds.builder()
                    //places.forEach { bounds.include(it.latLng) }
                    responseBody.forEach { if (it.name == "您的位置") bounds.include(it.latLng) }

                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 20))
                }
            }
        }
    }

    override fun onFail(message: String) {
        Log.d("MainActivity3", "onFailure$message")

    }
}

enum class TYPE {
    ATM,
    BANK
}

/*private fun expandCollapseSheet() {
    if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    } else {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

    }
}*/