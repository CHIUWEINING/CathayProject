package com.example.atry

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text


class CustomAdapter(private val mList: List<MyDataItem>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>()  {
    private lateinit var mListener:onItemClickListener
    interface onItemClickListener{
        fun onItemClick(item: MyDataItem)
    }
    fun setOnItemCLickListener(listener:onItemClickListener){
        mListener=listener
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

        val itemsViewModel = mList[position]
        holder.setData(itemsViewModel)

        // sets the image to the imageview from our itemHolder class
        //holder.imageView.setImageResource(ItemsViewModel.image)

        // sets the text to the textview from our itemHolder class
        holder.name.text = itemsViewModel.body
        holder.phone.text= itemsViewModel.id.toString()
        holder.addr.text= itemsViewModel.title

//        holder.itemView.context.getString(R.string.app_name)
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View, val listener:onItemClickListener ) : RecyclerView.ViewHolder(ItemView) {


        //val imageView: ImageView = itemView.findViewById(R.id.imageview)
        val name: TextView = itemView.findViewById(R.id.name)
        val phone: TextView=itemView.findViewById(R.id.phone)
        val addr: TextView=itemView.findViewById(R.id.addr)

//        init{
//            ItemView.setOnClickListener {
//                listener.onItemClick(bindingAdapterPosition)
//            }
//        }
        fun setData(data:MyDataItem){
//            textView.context.getString()
            itemView.setOnClickListener {
                listener.onItemClick(data)
            }
        }
    }
}