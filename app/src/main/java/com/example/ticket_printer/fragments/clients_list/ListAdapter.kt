//package com.example.ticket_printer.fragments.clients_list
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.example.ticket_printer.R
//import com.example.ticket_printer.data.Client
//
//class ListAdapter: RecyclerView.Adapter<ListAdapter.MyViewHolder>() {
//
//    private var clientList = emptyList<Client>()
//
//    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
//
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
//        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.custom_row, parent, false))
//    }
//
//    override fun getItemCount(): Int {
//        return clientList.size
//    }
//
//    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        var currentItem = clientList[position]
//        holder.itemView.findViewById<TextView>(R.id.id_text).text = currentItem.id.toString() + " -"
//        holder.itemView.findViewById<TextView>(R.id.name_text).text = currentItem.name
//        holder.itemView.findViewById<TextView>(R.id.phone_text).text = currentItem.phone
//
//    }
//
//    fun setData(client: List<Client>) {
//        this.clientList = client
//        notifyDataSetChanged()
//    }
//}

//package com.example.ticket_printer.fragments.clients_list
//
//import android.content.Intent
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.example.ticket_printer.R
//import com.example.ticket_printer.data.Client
//
//class ListAdapter : RecyclerView.Adapter<ListAdapter.MyViewHolder>() {
//
//    private var clientList = emptyList<Client>()
//
//    // Interface for item click listener
//    interface OnItemClickListener {
//        fun onItemClick(client: Client)
//    }
//
//    // Listener property
//    private var listener: OnItemClickListener? = null
//
//    // Method to set the listener
//    fun setOnItemClickListener(listener: OnItemClickListener) {
//        this.listener = listener
//    }
//
//    // ViewHolder class
//    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
//
//    // Inflate the layout for each item
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
//        return MyViewHolder(
//            LayoutInflater.from(parent.context).inflate(R.layout.custom_row, parent, false)
//        )
//    }
//
//    // Bind data to the views
//    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        val currentItem = clientList[position]
//
//        // Set data to the views
//        holder.itemView.findViewById<TextView>(R.id.id_text).text = currentItem.id.toString() + " -"
//        holder.itemView.findViewById<TextView>(R.id.name_text).text = currentItem.name
//        holder.itemView.findViewById<TextView>(R.id.phone_text).text = currentItem.phone
//
//        // Set click listener on the itemView
//        holder.itemView.setOnClickListener {
//            listener?.onItemClick(currentItem)
//        }
//    }
//
//    // Return the number of items in the list
//    override fun getItemCount(): Int {
//        return clientList.size
//    }
//
//    // Update the data in the adapter
//    fun setData(client: List<Client>) {
//        this.clientList = client
//        notifyDataSetChanged()
//    }
//}

package com.example.ticket_printer.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ticket_printer.R
import com.example.ticket_printer.data.Client

class ListAdapter : RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    private var clientList = emptyList<Client>()

    // Functional interface for item click listener
    fun interface OnItemClickListener {
        fun onItemClick(client: Client)
    }

    // Listener property
    private var listener: OnItemClickListener? = null

    // Method to set the listener
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    // ViewHolder class
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    // Inflate the layout for each item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.custom_row, parent, false)
        )
    }

    // Bind data to the views
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = clientList[position]

        // Set data to the views
        holder.itemView.findViewById<TextView>(R.id.id_text).text = currentItem.id.toString() + " -"
        holder.itemView.findViewById<TextView>(R.id.name_text).text = currentItem.lastName
        holder.itemView.findViewById<TextView>(R.id.phone_text).text = currentItem.phone

        // Set click listener on the itemView
        holder.itemView.setOnClickListener {
            listener?.onItemClick(currentItem)
        }
    }

    // Return the number of items in the list
    override fun getItemCount(): Int {
        return clientList.size
    }

    // Update the data in the adapter
    fun setData(client: List<Client>) {
        this.clientList = client
        notifyDataSetChanged()
    }
}