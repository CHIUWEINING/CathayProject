package com.example.atry.fragment

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.atry.*
import com.example.atry.contract.ContractBank
import com.example.atry.databinding.FragmentBankBinding
import com.example.atry.presenter.PresenterBank


class BankFragment : Fragment(), ContractBank.IViewAtm{
    private lateinit var binding:FragmentBankBinding
    private lateinit var presenter: ContractBank.IPresenterAtm
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentBankBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter= PresenterBank(this)
        presenter.getCounty()
        binding.spinnerCounty.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                p1: View?,
                position: Int,
                p3: Long
            ) {
                presenter.getDistrict(binding.spinnerCounty.selectedItem.toString())
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
        binding.buttonAssure.setDoneImage(R.drawable.ic_check_24dp)
        binding.buttonAssure.setOnClickListener {
            binding.buttonAssure.startAnimation()
            Handler().postDelayed({
                binding.buttonAssure.doneLoadingAnimation()
            },2000)
            Handler().postDelayed({
                (activity as? Home)?.run {
                    goTest(
                        null,
                        binding.spinnerDistrict.selectedItem.toString(),
                        binding.spinnerCounty.selectedItem.toString()
                    )
                }
            },3000)
        }
    }

    override fun adaptCounty(arrayCounty: Int) {
        var adapter = this@BankFragment.context?.let {
            ArrayAdapter.createFromResource(
                it, arrayCounty, android.R.layout.simple_spinner_dropdown_item
            )
        }
        binding.spinnerCounty.adapter = adapter
    }

    override fun adaptDistrict(arrayDistrict: Int) {
       var adapter = this@BankFragment.context?.let {
            ArrayAdapter.createFromResource(
                it,
                arrayDistrict,
                android.R.layout.simple_spinner_dropdown_item
            )
        }
        binding.spinnerDistrict.adapter = adapter
    }
}