package com.example.atry

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.AnimationDrawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.atry.databinding.ActivityHomeBinding
import com.example.atry.fragment.PageAdapter
import com.google.android.material.tabs.TabLayoutMediator

class Home : AppCompatActivity() ,Handler{
    private lateinit var binding: ActivityHomeBinding
    private lateinit var commandStr: String
    private lateinit var locationManager: LocationManager
    public val MY_PERMISSION_ACCESS_COARSE_LOCATION = 11
    public val MY_PERMISSION_ACCESS_FINE_LOCATION = 11
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        PageAdapter(supportFragmentManager, lifecycle).apply {
            binding.viewPager2.adapter = this
        }
        val title: ArrayList<String> = arrayListOf("ATM", "國泰分行")
        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            tab.text = title[position]
        }.attach()
        val animationDrawable = binding.constraintLayout.background as AnimationDrawable
        animationDrawable.setEnterFadeDuration(2000)
        animationDrawable.setExitFadeDuration(4000)
        animationDrawable.start()

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
            Map.myLng = location.longitude
            Map.myLat = location.latitude
        }
    }

    override fun goTest(serviceSelect:String?,districtSelect :String?,countySelect:String?) {
        startActivity(
            Intent(this, Map::class.java).apply {
                this.putExtra("service",serviceSelect)
                this.putExtra("district",districtSelect)
                this.putExtra("county",countySelect)
        })
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
}

interface Handler{
    fun goTest(serviceSelect:String?,districtSelect :String?,countySelect:String?)
}
enum class TYPE {
    ATM,
    BANK
}