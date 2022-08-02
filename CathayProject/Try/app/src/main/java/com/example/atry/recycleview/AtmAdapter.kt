package com.example.atry.recycleview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.atry.R
import com.example.atry.atm.AtmItem


class AtmAdapter() :
    RecyclerView.Adapter<AtmAdapter.ViewHolder>() {
    var mList: MutableList<AtmItem>?=null
    private lateinit var mListener: onItemClickListener
    interface onItemClickListener {
        fun onItemClick(item: AtmItem, view:View, position: Int)
    }

    fun setOnItemCLickListener(listener: onItemClickListener) {
        mListener = listener
    }

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_design, parent, false)

        return ViewHolder(view, mListener)

    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var animation: Animation = AnimationUtils.loadAnimation(holder.itemView.context,android.R.anim.slide_in_left)
        mList?.let{
            val itemsViewModel = it[position]
            holder.setData(itemsViewModel,position)
        }
        holder.itemView.startAnimation(animation)
        // sets the image to the imageview from our itemHolder class
        //holder.imageView.setImageResource(ItemsViewModel.image)
    }



    // return the number of the items in the list
    override fun getItemCount(): Int {
        if(mList==null){
            return 0
        }else return mList!!.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View, val listener: onItemClickListener) :
        RecyclerView.ViewHolder(ItemView) {


        //val imageView: ImageView = itemView.findViewById(R.id.imageview)
        val name: TextView = itemView.findViewById(R.id.name)
        val phone: TextView = itemView.findViewById(R.id.phone)
        val addr: TextView = itemView.findViewById(R.id.addr)

        fun setData(data: AtmItem, position:Int) {
//            textView.context.getString()
            name.text = "("+data.branchId+")"+data.name
            phone.text = data.kindname
            addr.text = data.address
            itemView.setOnClickListener {
                listener.onItemClick(data,itemView,position)
            }
        }
    }
}