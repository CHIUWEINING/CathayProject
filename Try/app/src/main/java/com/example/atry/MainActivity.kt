package com.example.atry

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.view.marginTop
import androidx.core.view.setMargins
import androidx.fragment.app.Fragment
import com.example.atry.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
//import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.tabs.TabLayoutMediator
import android.content.Intent as Intent1

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
        /*binding.tabLayout.addOnTabSelectedListener(object:TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    binding.viewPager2.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                TODO("Not yet implemented")
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                TODO("Not yet implemented")
            }
        })*/

        /*binding.buttonBank.setBackgroundColor(0xFF7F7F7F.toInt())
        //val Margin=binding.spinnerCounty.marginTop
        binding.buttonATM.setOnClickListener {
            val transaction=supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frgFragment, frgAtm).commit()
            binding.buttonATM.setBackgroundColor(0xFF4dc844.toInt())
            binding.buttonBank.setBackgroundColor(0xFF7F7F7F.toInt())
            *//*val layoutParams=binding.spinnerCounty.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.setMargins(168,Margin,0,0)
            binding.spinnerCounty.layoutParams=layoutParams*//*
        }
        binding.buttonBank.setOnClickListener {
            val transaction=supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frgFragment, frgBank).commit()
            binding.buttonBank.setBackgroundColor(0xFF4dc844.toInt())
            binding.buttonATM.setBackgroundColor(0xFF7F7F7F.toInt())
        }*/
    }
}