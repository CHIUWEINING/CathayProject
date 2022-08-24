package com.example.atry.filterDialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import com.example.atry.R
import com.example.atry.databinding.FilterDialogAtmBinding
import com.google.android.material.slider.Slider

class FilterDialogAtm(context:Context) : Dialog(context){
    private lateinit var binding: FilterDialogAtmBinding
    private var message: String?= null

    private var cancelListener: IOnCancelListener? = null
    private var confirmListener: IOnConfirmListener? = null


    fun setConfirm(Listener: IOnConfirmListener): FilterDialogAtm {
        this.confirmListener = Listener
        return this
    }
    fun setCancel(Listener: IOnCancelListener): FilterDialogAtm {
        this.cancelListener = Listener
        return this
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=FilterDialogAtmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.Sure.setOnClickListener(this::clickListener)
        binding.Cancel.setOnClickListener(this::clickListener)
        binding.slider.setLabelFormatter {
            "範圍：${it}公里"
        }
    }
    private fun clickListener(v: View){
        when(v.id){
            R.id.Sure -> {
                confirmListener?.let {
                    val checkArray= hashMapOf<String,Boolean>(
                        "qrCode" to binding.QRCode.isChecked
                        ,"cardLess" to binding.cardLess.isChecked
                        ,"iPass" to binding.iPass.isChecked
                        ,"visionImpaired" to binding.visionImpaired.isChecked
                        ,"coin" to binding.Coin.isChecked
                        ,"face" to binding.face.isChecked
                    )
                    it.onConfirm(checkArray)
                    dismiss()
                }
            }
            R.id.Cancel -> {
                cancelListener?.let {
                    it.onCancel(this)
                }
            }
        }
    }
    fun reset(checkArray: HashMap<String, Boolean>,range:Double){
        binding.Coin.isChecked=checkArray["coin"]==true
        binding.face.isChecked=checkArray["face"]==true
        binding.visionImpaired.isChecked=checkArray["visionImpaired"]==true
        binding.iPass.isChecked=checkArray["iPass"]==true
        binding.cardLess.isChecked=checkArray["cardLess"]==true
        binding.QRCode.isChecked=checkArray["qrCode"]==true
        binding.slider.value=range.toFloat()
    }
    interface IOnCancelListener {
        fun onCancel(dialog: FilterDialogAtm?)
    }

    interface IOnConfirmListener {
        fun onConfirm(checkArray:HashMap<String,Boolean>)
    }
}