package com.example.atry.recycleview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.atry.R
import com.example.atry.atm.AtmItem


class AtmAdapter() :
    RecyclerView.Adapter<AtmAdapter.ViewHolder>() {
    var mList: MutableList<AtmItem>?=null
    private val TYPE_TOP = 0x0000
    private val TYPE_NORMAL = 0x0001
    private lateinit var mListener: onItemClickListener
    interface onItemClickListener {
        fun onItemClick(item: AtmItem, view:View)
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
        var animation: Animation = AnimationUtils.loadAnimation(holder.itemView.findViewById<CardView>(R.id.cardView).context,android.R.anim.slide_in_left)
        mList?.let{
            val itemsViewModel = it[position]
            if (getItemViewType(position) == TYPE_TOP){
                // 第一行头的竖线不显示
                holder.tvTopLine.visibility=View.INVISIBLE
                holder.tvDot.setBackgroundResource(R.drawable.timelline_dot_first);
            }else if (getItemViewType(position) == TYPE_NORMAL){
                holder.tvTopLine.visibility=View.VISIBLE
                holder.tvDot.setBackgroundResource(R.drawable.timelline_dot_normal)
            }
            holder.setData(itemsViewModel,position)
        }
        holder.itemView.findViewById<CardView>(R.id.cardView).startAnimation(animation)
        // sets the image to the imageview from our itemHolder class
        //holder.imageView.setImageResource(ItemsViewModel.image)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            TYPE_TOP
        } else TYPE_NORMAL
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
        val tvDot=itemView.findViewById<TextView>(R.id.tvDot)
        val tvTopLine=itemView.findViewById<TextView>(R.id.tvTopLine)
        //val imageView: ImageView = itemView.findViewById(R.id.imageview)
        val name: TextView = itemView.findViewById(R.id.name)
        val phone: TextView = itemView.findViewById(R.id.phone)
        val addr: TextView = itemView.findViewById(R.id.addr)
        val dist: TextView = itemView.findViewById(R.id.distance)
        fun setData(data: AtmItem, position:Int) {
//            textView.context.getString()
            name.text = "("+data.branchId+")"+data.name
            phone.text = data.kindname
            addr.text = data.address
            dist.text= if(data.dist!! >1.0)data.dist.toString()+" 公里" else (data.dist*1000).toString()+" 公尺"
            itemView.setOnClickListener {
                listener.onItemClick(data,itemView)
            }
        }
    }
}