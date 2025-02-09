package com.example.ticket_printer.fragments.present_bikes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ticket_printer.R
import com.example.ticket_printer.data.UserViewModel
import com.example.ticket_printer.ui.ClientAdapter

class BikePresentFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ClientAdapter
    private lateinit var viewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bike_present, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewClients)
        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        // Set GridLayoutManager with 2 columns (adjust span count as needed)
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        adapter = ClientAdapter(emptyList(), viewModel, this)
        recyclerView.adapter = adapter


        viewModel.clientsWithBikes()?.observe(viewLifecycleOwner, Observer { clients ->
            // Update the adapter when data changes
            adapter.updateClients(clients)
        })
    }
}
