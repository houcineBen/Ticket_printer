// ClientAdapter.kt
package com.example.ticket_printer.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.ticket_printer.R
import com.example.ticket_printer.data.Client
import com.example.ticket_printer.data.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ClientAdapter(private var clients: List<Client>, private var viewModel: UserViewModel, private val lifecycleOwner: LifecycleOwner) :
    RecyclerView.Adapter<ClientAdapter.ClientViewHolder>() {

    class ClientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewClientName: TextView = itemView.findViewById(R.id.textViewClientName)
        val textViewClientContact: TextView = itemView.findViewById(R.id.textViewClientContact)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_client_card, parent, false)
        return ClientViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        val client = clients[position]
        var details = ""
        holder.textViewClientName.text = "${client.name} ${client.lastName}"
        lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            holder.textViewClientContact.text = viewModel.getLastVisitSync(client.id).bikeFixDetails
        }
    }

    override fun getItemCount(): Int = clients.size

    fun updateClients(newClients: List<Client>) {
        clients = newClients
        notifyDataSetChanged()
    }
}
