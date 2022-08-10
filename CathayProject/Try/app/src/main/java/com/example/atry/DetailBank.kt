package com.example.atry

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.widget.Toast
import androidx.core.view.ViewCompat.setTransitionName
import com.example.atry.databinding.ActivityDetailBankBinding


class DetailBank : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBankBinding
    private lateinit var myLat:String
    private lateinit var myLng:String
    private lateinit var endLat:String
    private lateinit var endLng:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBankBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var list = intent.getBundleExtra("list")?.getSerializable("list") as HashMap<String, String>
        myLat=list["myLat"]!!
        myLng=list["myLng"]!!
        endLat=list["endLat"]!!
        endLng=list["endLng"]!!
        binding.phone.text = list["phone"]
        binding.faxNo.text = if(list["fax"]==null) "本行無此服務" else "傳真："+list["fax"]
        binding.name.text = list["name"]
        binding.addr.text = if(list["addr"]?.length!! <=18)list["addr"] else list["addr"]?.substring(0,17)+"\n"+list["addr"]?.substring(17)
        binding.more1.text= list["more1"]
        binding.more2.text= list["more2"]
        setTransitionName(binding.name, Map.name_const)
        setTransitionName(binding.addr, Map.addr_const)
        val animationDrawable = binding.constraintLayout.background as AnimationDrawable
        animationDrawable.setEnterFadeDuration(2000)
        animationDrawable.setExitFadeDuration(4000)
        animationDrawable.start()
        binding.callBtn.setOnClickListener{
            val intent=Intent(Intent.ACTION_DIAL)
            val uri= Uri.parse("tel:"+binding.phone.text)
            intent.setData(uri)
            try {
                startActivity(intent)
                finish()
            } //撥打失敗的話，彈跳出失敗土司視窗
            catch (ex: ActivityNotFoundException) {
                Toast.makeText(
                    this@DetailBank,
                    "Call faild, please try again later.", Toast.LENGTH_LONG
                ).show()
            }
        }
        binding.navigation.setOnClickListener {
            val intent: Intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?" + "saddr=" + myLat.toDouble() + "," + myLng.toDouble() + "&daddr=" + endLat.toDouble() + "," + endLng.toDouble() + "&avoid=highway" + "&language=zh-CN")
            )
            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity")
            startActivity(intent)
        }
    }
    override fun onBackPressed() {
        val intent=Intent()
        /*val bundle1=Bundle()
        bundle1.putString("test",test)
        intent.putExtras(bundle1)*/
        setResult(Activity.RESULT_OK,intent)
        finish()
    }

}