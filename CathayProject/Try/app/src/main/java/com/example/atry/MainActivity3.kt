package com.example.atry

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Transition
import android.util.Log
import android.view.View
import android.view.Window
import androidx.core.view.ViewCompat.setTransitionName
import com.example.atry.databinding.ActivityMain3Binding
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback


class MainActivity3 : AppCompatActivity() {
    private lateinit var binding:ActivityMain3Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        binding = ActivityMain3Binding.inflate(layoutInflater)
        setContentView(binding.root)
        findViewById<View>(android.R.id.content).transitionName="share_element_container"
        setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        val test="test1212"
        var list= intent.getBundleExtra("list")?.getSerializable("list") as HashMap<String,String>
        val type=list["type"]
        if(type=="br")binding.phone.text=list["phone"]
        else binding.phone.text=list["kindname"]
        binding.name.text=list["name"]
        binding.addr.text=list["addr"]
        binding.test.setOnClickListener {
            val bundle1=Bundle()
            bundle1.putString("test",test)
            val intent=Intent()
            intent.putExtras(bundle1)
            setResult(Activity.RESULT_OK,intent)
            finish()
        }
       /* setTransitionName(binding.name,Map.name_const)
        setTransitionName(binding.phone,Map.phone_const)
        setTransitionName(binding.addr,Map.addr_const)*/
        window.sharedElementEnterTransition = com.google.android.material.transition.platform.MaterialContainerTransform()
            .apply {
            duration = 1000
            addTarget(android.R.id.content)
        }
        window.sharedElementReturnTransition = com.google.android.material.transition.platform.MaterialContainerTransform()
            .apply {
            duration = 1000
            addTarget(android.R.id.content)
        }
        val animationDrawable = binding.constraintLayout.background as AnimationDrawable
        animationDrawable.setEnterFadeDuration(2000)
        animationDrawable.setExitFadeDuration(4000)
        animationDrawable.start()
    }


}