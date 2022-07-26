package com.example.atry

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.atry.databinding.ActivityHomeBinding
import com.example.atry.fragment.PageAdapter
import com.google.android.material.tabs.TabLayoutMediator

class Home : AppCompatActivity() ,Handler{
    private lateinit var binding: ActivityHomeBinding
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
    }

    override fun goTest(serviceSelect:String?,districtSelect :String?,countySelect:String?) {
        startActivity(
            Intent(this, Map::class.java).apply {
                this.putExtra("service",serviceSelect)
                this.putExtra("district",districtSelect)
                this.putExtra("county",countySelect)
        })
    }
}

interface Handler{
    fun goTest(serviceSelect:String?,districtSelect :String?,countySelect:String?)
}