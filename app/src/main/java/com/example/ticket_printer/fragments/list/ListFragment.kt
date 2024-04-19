package com.example.ticket_printer.fragments.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ticket_printer.R
import com.example.ticket_printer.data.Client
import com.example.ticket_printer.data.UserViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton


class ListFragment : Fragment() {

    private lateinit var mClientViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_list, container, false)

//        recyclerView
        val adapter = ListAdapter()
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

//        ClientViewModel
        mClientViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        mClientViewModel.readAllData.observe(viewLifecycleOwner, Observer { client ->
            adapter.setData(client)
        })


        val btn: FloatingActionButton = view.findViewById(R.id.home_button)
        btn.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_printFragment)
        }

        return view
    }

}