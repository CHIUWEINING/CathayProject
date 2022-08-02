package com.example.atry.filterDialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import com.example.atry.R
import com.example.atry.databinding.FilterDialogAtmBinding

class FilterDialogAtm(context:Context) : Dialog(context){
    private lateinit var binding: FilterDialogAtmBinding
    private var message: String?= null

    private var cancelListener: IOnCancelListener? = null
    private var confirmListener: IOnConfirmListener? = null

    fun setMessage(message: String?): FilterDialogAtm {
        this.message = message
        return this
    }

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
        binding.Sure.setOnClickListener {
            confirmListener?.let{
                val checkArray= arrayOf<Boolean>(binding.QRCode.isChecked
                    ,binding.koko.isChecked
                    ,binding.Line.isChecked
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
                        ,binding.koko.isChecked
                        ,binding.Line.isChecked
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
        fun onCancel(dialog: FilterDialogAtm?)
    }

    interface IOnConfirmListener {
        fun onConfirm(checkArray:Array<Boolean>)
    }
}