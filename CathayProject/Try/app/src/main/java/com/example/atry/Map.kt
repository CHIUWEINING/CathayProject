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
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
        var myLng by Delegates.notNull<Double>()
        var myLat by Delegates.notNull<Double>()
    }

    private lateinit var binding: ActivityMapBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var presenter: ContractMap.IPresenter2
    private lateinit var locationManager: LocationManager
    private lateinit var commandStr: String
    private lateinit var filterDialogAtm: FilterDialogAtm
    var backupAtm = hashMapOf<String, Boolean>()
    var backupBr = arrayOf<Boolean>()
    private lateinit var filterDialogBank: FilterDialogBank
    public val MY_PERMISSION_ACCESS_COARSE_LOCATION = 11
    public val MY_PERMISSION_ACCESS_FINE_LOCATION = 11

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
        filterDialogBank = FilterDialogBank(this)
        filterDialogAtm = FilterDialogAtm(this)
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
                var passList = hashMapOf(
                    "name" to "(" + item.branchId + ")" + item.name,
                    "addr" to "(" + item.zipCode + ")" + item.address,
                    "phone" to "電話：" + item.teleNo,
                    "fax" to item.faxNo,
                    "more1" to "保險箱：" + if (item.safetyBox == "") "無" else "有",
                    "more2" to "指定外匯分行：" + if (item.isfx == "") "無" else "有",
                    "myLng" to myLng.toString(),
                    "myLat" to myLat.toString(),
                    "endLng" to item.latLng.longitude.toString(),
                    "endLat" to item.latLng.latitude.toString()
                )
                val box = Bundle()
                box.putSerializable("list", passList)
                val intent = Intent(this@Map, DetailBank::class.java)
                intent.putExtra("list", box)
                view.transitionName = "share_element_container"
                val options = ActivityOptions.makeSceneTransitionAnimation(
                    this@Map,
                    view,
                    "share_element_container"
                )
                startActivityForResult(intent, 1, options.toBundle())
            }
        })

        atmAdapter.setOnItemCLickListener(object : AtmAdapter.onItemClickListener {
            override fun onItemClick(item: AtmItem, view: View) {
                var passList = hashMapOf(
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
                    BottomSheetBehavior.STATE_HIDDEN -> {}
                    BottomSheetBehavior.STATE_EXPANDED -> {}
                    BottomSheetBehavior.STATE_COLLAPSED -> {}
                    BottomSheetBehavior.STATE_DRAGGING -> {}
                    BottomSheetBehavior.STATE_SETTLING -> {}
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {}
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
        ServiceSelect = intent.getStringExtra("service").toString()
        commandStr = LocationManager.NETWORK_PROVIDER
        getMyPosition()
        locationManager.requestLocationUpdates(commandStr, 1000, 0f, object : LocationListener {
            override fun onLocationChanged(p0: Location) {
                println(p0.longitude)
                println(p0.latitude)
            }
        })
        var location = locationManager.getLastKnownLocation(commandStr)
        if (location != null) {
            myLng = location.longitude
            myLat = location.latitude
            presenter.getDataSimple(ServiceSelect, myLng, myLat)
        }

        binding.progressBar.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
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
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (ServiceSelect == "ATM") {
                binding.progressBar.visibility = View.VISIBLE
                presenter.getDataAtm(
                    atmRequest =
                    AtmRequest(
                        25.038835000,
                        121.568656000,
                        filterDialogAtm.findViewById<Slider>(R.id.slider).value.toDouble(),
                        "0",
                        "0",
                        ipass = if (backupAtm["iPass"] == true) "1" else "0",
                        visionimpaired = if (backupAtm["visionImpaired"] == true) "1" else "0",
                        cardless = if (backupAtm["cardLess"] == true) "1" else "0",
                        qrcode = if (backupAtm["qrCode"] == true) "1" else "0",
                        coin = if (backupAtm["coin"] == true) "1" else "0",
                        face = if (backupAtm["face"] == true) "1" else "0"
                    )
                )
            } else {
                binding.progressBar.visibility = View.VISIBLE
                presenter.getDataBr(
                    brRequest = BrRequest(
                        25.038835000,
                        121.568656000,
                        filterDialogBank.findViewById<Slider>(R.id.slider).value.toDouble(),
                        safety_box = if (backupBr[1]) "Y" else "",
                        isfx = if (backupBr[0]) "Y" else ""
                    )
                )
            }
        }
    }

    fun getMyPosition() {
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSION_ACCESS_FINE_LOCATION
            )
        }
    }


    private fun showDialog() {
        if (ServiceSelect == "ATM") {
            filterDialogAtm
                .setConfirm(object : FilterDialogAtm.IOnConfirmListener {
                    override fun onConfirm(checkArray: HashMap<String, Boolean>) {
                        //get the result of checkbox filter
                        binding.progressBar.visibility = View.VISIBLE
                        presenter.getDataAtm(
                            atmRequest =
                            AtmRequest(
                                25.038835000,
                                121.568656000,
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
                        filterDialogAtm.hide()
                    }
                })
                .setCancel(object : FilterDialogAtm.IOnCancelListener {
                    override fun onCancel(dialog: FilterDialogAtm?) {
                        filterDialogAtm.reset(backupAtm)
                        filterDialogAtm.hide()
                    }
                }).show()
            filterDialogAtm.setCancelable(false)
        } else {
            filterDialogBank
                .setConfirm(object : FilterDialogBank.IOnConfirmListener {
                    override fun onConfirm(checkArray: Array<Boolean>) {
                        //get the result of checkbox filter
                        binding.progressBar.visibility = View.VISIBLE
                        presenter.getDataBr(
                            brRequest = BrRequest(
                                25.038835000,
                                121.568656000,
                                filterDialogBank.findViewById<Slider>(R.id.slider).value.toDouble(),
                                safety_box = if (checkArray[1]) "Y" else "",
                                isfx = if (checkArray[0]) "Y" else ""
                            )
                        )
                        backupBr = checkArray
                        filterDialogBank.hide()
                    }
                })
                .setCancel(object : FilterDialogBank.IOnCancelListener {
                    override fun onCancel(dialog: FilterDialogBank?) {
                        filterDialogBank.reset(backupBr)
                        filterDialogBank.hide()
                    }
                }).show()
            filterDialogBank.setCancelable(false)
        }

    }


    private fun <T> addMarkers(googleMap: GoogleMap, responseBody: MutableList<T>) {
        googleMap.clear()
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
                    } else {
                        val marker = googleMap.addMarker(
                            MarkerOptions()
                                .title(item.name)
                                .position(item.latLng)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bank_map))
                        )
                        marker?.tag = item
                    }
                }
            }
            googleMap.setOnInfoWindowClickListener {
                it.tag?.let {
                    when (it) {
                        is AtmItem -> {
                            var layoutManager =
                                binding.persistentBottomSheet.recyclerview.layoutManager as LinearLayoutManager
                            atmAdapter.mList?.forEachIndexed { id, item ->
                                if (item.name == it.name && item.address == it.address) {
                                    layoutManager.scrollToPositionWithOffset(id, 0)
                                }
                            }
                        }
                        is BranchItem -> {
                            var layoutManager =
                                binding.persistentBottomSheet.recyclerview.layoutManager as LinearLayoutManager
                            branchAdapter.mList?.forEachIndexed { id, item ->
                                if (item.name == it.name && item.address == it.address) {
                                    layoutManager.scrollToPositionWithOffset(id, 0)
                                }
                            }
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
                            LatLng(25.038835000, 121.568656000),
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
                    val now =
                        BranchItem(
                            "您的位置",
                            LatLng(25.038835000, 121.568656000),
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
                    LatLng(25.038835000, 121.568656000),
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