package com.example.atry

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import com.example.atry.databinding.FilterDialogBinding

class FilterDialog(context:Context) : Dialog(context){
    private lateinit var binding:FilterDialogBinding
    private var message: String?= null

    private var cancelListener: IOnCancelListener? = null
    private var confirmListener: IOnConfirmListener? = null

    fun setMessage(message: String?): FilterDialog {
        this.message = message
        return this
    }

    fun setConfirm(Listener: IOnConfirmListener): FilterDialog {
        this.confirmListener = Listener
        return this
    }
    fun setCancel(Listener: IOnCancelListener): FilterDialog{
        this.cancelListener = Listener
        return this
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=FilterDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.Sure.setOnClickListener {
            confirmListener?.let{
                val checkArray= arrayOf<Boolean>(binding.QRCode.isChecked
                    ,binding.Hours.isChecked
                    ,binding.twoHundred.isChecked
                    ,binding.NoCard.isChecked
                    ,binding.OneCard.isChecked
                    ,binding.eye.isChecked
                    ,binding.Change.isChecked
                    ,binding.face.isChecked
                )
                it.onConfirm(checkArray)
            }
        }
        binding.Cancel.setOnClickListener(this::clickListener)
    }
    private fun clickListener(v: View){
        when(v.id){
            R.id.Sure -> {
                confirmListener?.let {
                    val checkArray= arrayOf<Boolean>(binding.QRCode.isChecked
                        ,binding.Hours.isChecked
                        ,binding.twoHundred.isChecked
                        ,binding.NoCard.isChecked
                        ,binding.OneCard.isChecked
                        ,binding.eye.isChecked
                        ,binding.Change.isChecked
                        ,binding.face.isChecked
                    )
                    it.onConfirm(checkArray)
                }
            }
            R.id.Cancel -> {
                cancelListener?.let {
                    it.onCancel(this)
                }
            }
        }
    }
    interface IOnCancelListener {
        fun onCancel(dialog: FilterDialog?)
    }

    interface IOnConfirmListener {
        fun onConfirm(checkArray:Array<Boolean>)
    }
}