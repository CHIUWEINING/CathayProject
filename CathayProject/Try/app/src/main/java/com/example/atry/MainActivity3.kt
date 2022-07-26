package com.example.atry

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.atry.databinding.ActivityMain3Binding


class MainActivity3 : AppCompatActivity() {
    private lateinit var binding:ActivityMain3Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain3Binding.inflate(layoutInflater)
        setContentView(binding.root)
        val test="test1212"
        var list= intent.getBundleExtra("list")?.getSerializable("list") as HashMap<String,String>
        binding.name.text=list["name"]
        binding.addr.text=list["addr"]
        binding.phone.text=list["phone"]
        binding.test.setOnClickListener {
            val bundle1=Bundle()
            bundle1.putString("test",test)
            val intent=Intent()
            intent.putExtras(bundle1)
            setResult(Activity.RESULT_OK,intent)
            finish()
        }
    }


}