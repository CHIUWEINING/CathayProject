package com.example.atry

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.atry.databinding.ActivityMainBinding
import com.example.atry.fragment.PageAdapter
//import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val pageAdapter = PageAdapter(supportFragmentManager, lifecycle)
        binding.viewPager2.adapter = pageAdapter
        val title: ArrayList<String> = arrayListOf("ATM", "國泰分行")
        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            tab.text = title[position]
        }.attach()

    }
}