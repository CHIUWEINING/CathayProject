package com.example.atry.fragment

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.atry.*
import com.example.atry.contract.ContractAtm
import com.example.atry.databinding.FragmentAtmBinding
import com.example.atry.presenter.PresenterAtm


class AtmFragment : Fragment(), ContractAtm.IViewAtm {
    private lateinit var binding: FragmentAtmBinding
    private lateinit var presenter: PresenterAtm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAtmBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adaptService(R.array.service)
        presenter = PresenterAtm(this)
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
                        binding.spinnerService.selectedItem.toString(),
                        binding.spinnerDistrict.selectedItem.toString(),
                        binding.spinnerCounty.selectedItem.toString()
                    )
                }
            },3000)
        }

    }
    private fun adaptService(arrayService: Int) {
        binding.spinnerService.setAdapter(arrayService)
    }

    override fun adaptCounty(arrayCounty: Int) {
        binding.spinnerCounty.setAdapter(arrayCounty)
    }

    override fun adaptDistrict(arrayDistrict: Int) {
        binding.spinnerDistrict.setAdapter(arrayDistrict)
    }

    private fun Spinner.setAdapter(arrayDistrict: Int) {
        this.context?.let {
            ArrayAdapter.createFromResource(
                it, arrayDistrict, android.R.layout.simple_spinner_dropdown_item
            ).apply {
                this@setAdapter.adapter = this
            }
        }
    }
}