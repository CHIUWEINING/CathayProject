package com.example.atry.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import com.example.atry.MainActivity2
import com.example.atry.R
import com.example.atry.databinding.FragmentAtmBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Atm.newInstance] factory method to
 * create an instance of this fragment.
 */
class Atm : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentAtmBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAtmBinding.inflate(layoutInflater, container, false)
        var adapter = this.context?.let {
            ArrayAdapter.createFromResource(
                it,
                R.array.service,
                android.R.layout.simple_spinner_dropdown_item
            )
        }
        binding.spinnerService.adapter = adapter
        adapter = this@Atm.context?.let {
            ArrayAdapter.createFromResource(
                it, R.array.District, android.R.layout.simple_spinner_dropdown_item
            )
        }
        binding.spinnerCounty.adapter = adapter
        var countySelect:String?=null
        var districtSelect:String?=null
        binding.spinnerCounty.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                p1: View?,
                position: Int,
                p3: Long
            ) {
                countySelect = parent?.getItemAtPosition(position).toString()
                if (countySelect == "Taipei") {
                    adapter = this@Atm.context?.let {
                        ArrayAdapter.createFromResource(
                            it,
                            R.array.Taipei,
                            android.R.layout.simple_spinner_dropdown_item
                        )
                    }
                    binding.spinnerDistrict.adapter = adapter
                } else if (countySelect == "New Taipei") {
                    adapter = this@Atm.context?.let {
                        ArrayAdapter.createFromResource(
                            it,
                            R.array.Ｎew_Taipei,
                            android.R.layout.simple_spinner_dropdown_item
                        )
                    }
                    binding.spinnerDistrict.adapter = adapter
                }

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
        binding.spinnerDistrict.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    p1: View?,
                    position: Int,
                    p3: Long
                ) {
                    districtSelect = parent?.getItemAtPosition(position).toString()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }
        var serviceSelect:String?=null
        binding.spinnerService.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    p1: View?,
                    position: Int,
                    p3: Long
                ) {
                    serviceSelect = parent?.getItemAtPosition(position).toString()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }
        binding.buttonAssure.setOnClickListener {
            if (serviceSelect != null && districtSelect != null && countySelect != null) {
                startActivity(Intent(context, MainActivity2::class.java).apply {
                    var passList= hashMapOf("service" to serviceSelect
                                        ,"district" to districtSelect
                                        ,"county" to countySelect)
                    val box=Bundle()
                    box.putSerializable("list",passList)
                    putExtra("list",box)
                })
            }

        }
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Atm.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Atm().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}