package com.example.ticket_printer.fragments.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ticket_printer.R
import com.example.ticket_printer.data.Client

class ListAdapter: RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    private var clientList = emptyList<Client>()

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.custom_row, parent, false))
    }

    override fun getItemCount(): Int {
        return clientList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var currentItem = clientList[position]
        holder.itemView.findViewById<TextView>(R.id.id_text).text = currentItem.id.toString() + " -"
        holder.itemView.findViewById<TextView>(R.id.name_text).text = currentItem.name
        holder.itemView.findViewById<TextView>(R.id.phone_text).text = currentItem.phone

    }

    fun setData(client: List<Client>) {
        this.clientList = client
        notifyDataSetChanged()
    }
}