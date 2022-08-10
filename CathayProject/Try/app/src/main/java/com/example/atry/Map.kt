package com.example.atry


import android.Manifest
import android.app.Activity
import android.app.ActivityOptions
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.atry.atm.AtmItem
import com.example.atry.atm.AtmRequest
import com.example.atry.branch.BrRequest
import com.example.atry.branch.BranchItem
import com.example.atry.contract.ContractMap
import com.example.atry.databinding.ActivityMapBinding
import com.example.atry.filterDialog.FilterDialogAtm
import com.example.atry.filterDialog.FilterDialogBank
import com.example.atry.mapTool.BitmapHelper
import com.example.atry.mapTool.MarkerInfoWindowAdapter
import com.example.atry.presenter.PresenterMap
import com.example.atry.recycleview.AtmAdapter
import com.example.atry.recycleview.BranchAdapter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.slider.Slider
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import kotlin.properties.Delegates

//172.25.137.68
class Map : AppCompatActivity(), ContractMap.IView2 {

    companion object {
        const val name_const = "name"
        const val phone_const = "phone"
        const val addr_const = "addr"
        var loading = true
        var myLng:Double?=null
        var myLat:Double?=null
    }

    private lateinit var binding: ActivityMapBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var presenter: ContractMap.IPresenter2
    private lateinit var filterDialogAtm: FilterDialogAtm
    private lateinit var myGoogleMap:GoogleMap
    var backupAtm = hashMapOf<String, Boolean>()
    var backupBr = arrayOf<Boolean>()
    private lateinit var filterDialogBank: FilterDialogBank
    private var atmRange=1.0
    private var brRange=5.0

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
    private var markerList= mutableListOf<Marker>()
    private lateinit var ServiceSelect: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("create")
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
            override fun onItemClick(item: BranchItem, view: View) {
                println(item)
                markerList.forEach {
                    val myItem=it.tag as BranchItem
                    if(item.name==myItem.name && item.address==myItem.address && item.latLng==myItem.latLng){
                        //move Camera
                        myGoogleMap.moveCamera(
                            (CameraUpdateFactory.newLatLngZoom(
                                item.latLng,
                                17f
                            ))
                        )
                        it.showInfoWindow()
                        bottomSheetBehavior.state=BottomSheetBehavior.STATE_HALF_EXPANDED
                    }
                }
            }
        })

        atmAdapter.setOnItemCLickListener(object : AtmAdapter.onItemClickListener {
            override fun onItemClick(item: AtmItem, view: View) {
                markerList.forEach {
                    val myItem=it.tag as AtmItem
                    if(item.sno==myItem.sno){
                        //move Camera
                        myGoogleMap.moveCamera(
                            (CameraUpdateFactory.newLatLngZoom(
                                item.latLng,
                                17f
                            ))
                        )
                        it.showInfoWindow()
                        bottomSheetBehavior.state=BottomSheetBehavior.STATE_HALF_EXPANDED
                    }
                }
                /*var passList = hashMapOf(
                    "name" to "(" + item.branchId + ")" + item.name,
                    "addr" to item.address,
                    "kindname" to item.kindname,
                    "phone" to item.teleNo,
                    "more1" to "QRCode:" + if (item.qrCode == "0") "無" else "有",
                    "more2" to "一卡通服務:" + if (item.iPass == "0") "無" else "有",
                    "more3" to "零錢服務:" + if (item.coin == "0") "無" else "有",
                    "more4" to "無卡提款：" + if (item.cardLess == "0") "無" else "有",
                    "more5" to "視障服務：" + if (item.visionImpaired == "0") "無" else "有",
                    "more6" to "人臉辨識：" + if (item.face == "0") "無" else "有",
                    "myLng" to myLng.toString(),
                    "myLat" to myLat.toString(),
                    "endLng" to item.latLng.longitude.toString(),
                    "endLat" to item.latLng.latitude.toString()
                )
                val box = Bundle()
                box.putSerializable("list", passList)
                val intent = Intent(this@Map, DetailAtm::class.java)
                intent.putExtra("list", box)*/


                /*val navigation=view.findViewById<View>(R.id.navigation)
                navigation.setOnClickListener {
                    val intent: Intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?" + "saddr=" + myLat.toDouble() + "," + myLng.toDouble() + "&daddr=" + item.latLng.longitude + "," + item.latLng.latitude + "&avoid=highway" + "&language=zh-CN")
                    )
                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity")
                    startActivity(intent)
                }*/
                /*val activityOptions=ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this@Map,
                    Pair(nameView, name_const),
                    Pair(phoneView, phone_const),
                    Pair(addrView, addr_const)
                    )*/



                /*view.transitionName = "share_element_container"
                val options = ActivityOptions.makeSceneTransitionAnimation(
                    this@Map,
                    view,
                    "share_element_container"
                )
                startActivityForResult(intent, 1, options.toBundle())*/
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
                    BottomSheetBehavior.STATE_HIDDEN -> {}
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        binding.persistentBottomSheet.dist.visibility=View.VISIBLE
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding.persistentBottomSheet.dist.visibility=View.INVISIBLE
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {}
                    BottomSheetBehavior.STATE_SETTLING -> {}
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                        binding.persistentBottomSheet.dist.visibility=View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
        ServiceSelect = intent.getStringExtra("service").toString()
        presenter.getDataSimple(ServiceSelect, myLng!!, myLat!!)
        binding.progressBar.visibility = View.VISIBLE

    }

    override fun onResume() {
        super.onResume()
        println("resume")
        /*backupAtm = hashMapOf<String, Boolean>()
        backupBr = arrayOf<Boolean>()*/
        val displayMetrics: DisplayMetrics = this.resources.displayMetrics
        val height = displayMetrics.heightPixels
        val peekHeight = height * 0.06
        bottomSheetBehavior.peekHeight = peekHeight.toInt()
        //todo adapter init 先做
        if (ServiceSelect == "BANK") binding.persistentBottomSheet.recyclerview.adapter =
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
        /*data.extras.let 取得bundle*/
        println("result")
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            //回來要做的事
        }
    }


    private fun showDialog() {
        if (ServiceSelect == "ATM") {
            filterDialogAtm = FilterDialogAtm(this)
            filterDialogAtm.show()
            filterDialogAtm.hide()
            filterDialogAtm.reset(backupAtm,atmRange)
            filterDialogAtm
                .setConfirm(object : FilterDialogAtm.IOnConfirmListener {
                    override fun onConfirm(checkArray: HashMap<String, Boolean>) {
                        //get the result of checkbox filter
                        binding.progressBar.visibility = View.VISIBLE
                        presenter.getDataAtm(
                            atmRequest =
                            AtmRequest(
                                myLat!!,
                                myLng!!,
                                filterDialogAtm.findViewById<Slider>(R.id.slider).value.toDouble(),
                                "0",
                                "0",
                                ipass = if (checkArray["iPass"] == true) "1" else "0",
                                visionimpaired = if (checkArray["visionImpaired"] == true) "1" else "0",
                                cardless = if (checkArray["cardLess"] == true) "1" else "0",
                                qrcode = if (checkArray["qrCode"] == true) "1" else "0",
                                coin = if (checkArray["coin"] == true) "1" else "0",
                                face = if (checkArray["face"] == true) "1" else "0"
                            )
                        )
                        backupAtm = checkArray
                        atmRange=filterDialogAtm.findViewById<Slider>(R.id.slider).value.toDouble()
                        filterDialogAtm.dismiss()
                    }
                })
                .setCancel(object : FilterDialogAtm.IOnCancelListener {
                    override fun onCancel(dialog: FilterDialogAtm?) {
                        filterDialogAtm.dismiss()
                    }
                }).show()
            filterDialogAtm.setCancelable(false)
        } else {
            filterDialogBank = FilterDialogBank(this)
            filterDialogBank.show()
            filterDialogBank.hide()
            filterDialogBank.reset(backupBr,brRange)
            filterDialogBank
                .setConfirm(object : FilterDialogBank.IOnConfirmListener {
                    override fun onConfirm(checkArray: Array<Boolean>) {
                        //get the result of checkbox filter
                        binding.progressBar.visibility = View.VISIBLE
                        presenter.getDataBr(
                            brRequest = BrRequest(
                                myLat!!,
                                myLng!!,
                                filterDialogBank.findViewById<Slider>(R.id.slider).value.toDouble(),
                                safety_box = if (checkArray[1]) "Y" else "",
                                isfx = if (checkArray[0]) "Y" else ""
                            )
                        )
                        backupBr = checkArray
                        brRange= filterDialogBank.findViewById<Slider>(R.id.slider).value.toDouble()
                        filterDialogBank.dismiss()
                    }
                })
                .setCancel(object : FilterDialogBank.IOnCancelListener {
                    override fun onCancel(dialog: FilterDialogBank?) {
                        filterDialogBank.dismiss()
                    }
                }).show()
            filterDialogBank.setCancelable(false)
        }

    }


    private fun <T> addMarkers(googleMap: GoogleMap, responseBody: MutableList<T>) {
        googleMap.clear()
        markerList.clear()
        responseBody.forEach { item ->
            when (item) {
                is AtmItem -> {
                    val markerInfoWindowAdapter = MarkerInfoWindowAdapter(this, 2)
                    googleMap.setInfoWindowAdapter(markerInfoWindowAdapter)
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
                        markerList.add(marker!!)
                    }
                }
                is BranchItem -> {
                    val markerInfoWindowAdapter = MarkerInfoWindowAdapter(this, 1)
                    googleMap.setInfoWindowAdapter(markerInfoWindowAdapter)
                    if (item.name == "您的位置") {
                        val marker = googleMap.addMarker(
                            MarkerOptions()
                                .title(item.name)
                                .position(item.latLng)
                        )
                        // Set place as the tag on the marker object so it can be referenced within
                        // MarkerInfoWindowAdapter
                        marker?.tag = item
                        marker?.showInfoWindow()
                    } else {
                        val marker = googleMap.addMarker(
                            MarkerOptions()
                                .title(item.name)
                                .position(item.latLng)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bank_map))
                        )
                        marker?.tag = item
                        markerList.add(marker!!)
                    }
                }
            }
            googleMap.setOnInfoWindowClickListener {
                it.tag?.let {
                    when (it) {
                        is AtmItem -> {
                            var count=0
                            var passList = hashMapOf(
                                "name" to "(" + it.branchId + ")" + it.name,
                                "addr" to it.address,
                                "kindname" to it.kindname,
                                "phone" to it.teleNo,
                                "more1" to "QRCode:" + if (it.qrCode == "0") "無" else "有",
                                "more2" to "一卡通服務:" + if (it.iPass == "0") "無" else "有",
                                "more3" to "零錢服務:" + if (it.coin == "0") "無" else "有",
                                "more4" to "無卡提款：" + if (it.cardLess == "0") "無" else "有",
                                "more5" to "視障服務：" + if (it.visionImpaired == "0") "無" else "有",
                                "more6" to "人臉辨識：" + if (it.face == "0") "無" else "有",
                                "myLng" to myLng.toString(),
                                "myLat" to myLat.toString(),
                                "endLng" to it.latLng.longitude.toString(),
                                "endLat" to it.latLng.latitude.toString()
                            )
                            val box = Bundle()
                            box.putSerializable("list", passList)
                            val intent = Intent(this@Map, DetailAtm::class.java)
                            intent.putExtra("list", box)
                            markerList.forEachIndexed { index, marker ->
                                val markerInfoWindowAdapter=MarkerInfoWindowAdapter(this,2)
                                val view=markerInfoWindowAdapter.getInfoContents(marker)
                                if(marker.position==it.latLng  && count==0){
                                    count++
                                    val nameView=view?.findViewById<TextView>(R.id.text_view_title)
                                    val addrView=view?.findViewById<TextView>(R.id.text_view_address)
                                    val options= ActivityOptionsCompat.makeSceneTransitionAnimation(
                                        this@Map,
                                        Pair(nameView, name_const),
                                        Pair(addrView, addr_const)
                                    )
                                    startActivityForResult(intent, 1,options.toBundle())
                                }
                            }
                        }
                        is BranchItem -> {
                            var count=0
                            var passList = hashMapOf(
                                "name" to "(" + it.branchId + ")" + it.name,
                                "addr" to "(" + it.zipCode + ")" + it.address,
                                "phone" to "電話：" + it.teleNo,
                                "fax" to it.faxNo,
                                "more1" to "保險箱：" + if (it.safetyBox == "") "無" else "有",
                                "more2" to "指定外匯分行：" + if (it.isfx == "") "無" else "有",
                                "myLng" to myLng.toString(),
                                "myLat" to myLat.toString(),
                                "endLng" to it.latLng.longitude.toString(),
                                "endLat" to it.latLng.latitude.toString()
                            )
                            val box = Bundle()
                            box.putSerializable("list", passList)
                            val intent = Intent(this@Map, DetailBank::class.java)
                            intent.putExtra("list", box)
                            markerList.forEachIndexed { index, marker ->
                                val markerInfoWindowAdapter=MarkerInfoWindowAdapter(this,1)
                                val view=markerInfoWindowAdapter.getInfoContents(marker)
                                if(marker.position==it.latLng && count==0){
                                    count++
                                    val nameView=view?.findViewById<TextView>(R.id.text_view_title)
                                    val addrView=view?.findViewById<TextView>(R.id.text_view_address)
                                    val options= ActivityOptionsCompat.makeSceneTransitionAnimation(
                                        this@Map,
                                        Pair(nameView, name_const),
                                        Pair(addrView, addr_const)
                                    )
                                    startActivityForResult(intent, 1,options.toBundle())
                                }
                            }
                            /*var layoutManager =
                                binding.persistentBottomSheet.recyclerview.layoutManager as LinearLayoutManager
                            branchAdapter.mList?.forEachIndexed { id, item ->
                                if (item.name == it.name && item.address == it.address) {
                                    layoutManager.scrollToPositionWithOffset(id, 0)
                                }
                            }*/
                        }
                    }
                }
            }
        }
    }

    override fun onSuccess(responseBody: MutableList<Any>) {
        if (responseBody.size > 0) {
            when (responseBody[0]) {
                is AtmItem -> {
                    atmAdapter.mList = responseBody.toMutableList() as MutableList<AtmItem>
                    atmAdapter.notifyDataSetChanged()
                    val now =
                        AtmItem(
                            "您的位置",
                            "",
                            LatLng(myLat!!, myLng!!),
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            0.0
                        )
                    responseBody?.let {
                        it.add(now)
                    }
                }
                else -> {
                    branchAdapter.mList = responseBody.toMutableList() as MutableList<BranchItem>
                    branchAdapter.notifyDataSetChanged()
                    println("change")
                    val now =
                        BranchItem(
                            "您的位置",
                            LatLng(myLat!!, myLng!!),
                            "",
                            "",
                            "",
                            "",
                            "",
                            0.0,
                            "",
                            ""
                        )
                    responseBody?.let {
                        it.add(now)
                    }
                }
            }
        } else {
            atmAdapter.mList = responseBody.toMutableList() as MutableList<AtmItem>
            atmAdapter.notifyDataSetChanged()
            branchAdapter.mList = responseBody.toMutableList() as MutableList<BranchItem>
            branchAdapter.notifyDataSetChanged()
            val now =
                BranchItem(
                    "您的位置",
                    LatLng(myLat!!, myLng!!),
                    "",
                    "",
                    "",
                    "",
                    "",
                    0.0,
                    "",
                    ""
                )
            responseBody?.let {
                it.add(now)
            }
            Snackbar.make(
                binding.root,
                "抱歉！我們無法找到符合您需求的服務據點。\n您可嘗試減少限制或將增加搜尋範圍",
                Snackbar.LENGTH_LONG
            ).show()
        }
        (supportFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment)?.run {
            getMapAsync { googleMap ->
                //addMarkers(googleMap)
                myGoogleMap=googleMap
                googleMap.setInfoWindowAdapter(MarkerInfoWindowAdapter(this@Map, 1))
                addMarkers(googleMap, responseBody)

                // Set custom info window adapter.
                googleMap.setOnMapLoadedCallback {
                    //val bounds = LatLngBounds.builder()
                    //places.forEach { bounds.include(it.latLng) }
                    responseBody.forEach {
                        when (it) {
                            is AtmItem -> {
                                if (it.name == "您的位置") {
                                    //bounds.include(it.latLng)
                                    googleMap.moveCamera(
                                        (CameraUpdateFactory.newLatLngZoom(
                                            it.latLng,
                                            17f
                                        ))
                                    )
                                }
                            }
                            is BranchItem -> {
                                if (it.name == "您的位置") googleMap.moveCamera(
                                    (CameraUpdateFactory.newLatLngZoom(
                                        it.latLng,
                                        14f
                                    ))
                                )
                            }
                        }
                    }
                    /*googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 20))*/
                }
            }
        }
        loading = false
        binding.progressBar.visibility = View.INVISIBLE
    }

    override fun onFail(message: String) {
        Log.d("MainActivity3", "onFailure$message")
        Snackbar.make(binding.root, "無法取得相關資料，請稍後再試。", Snackbar.LENGTH_LONG).show()
    }
}