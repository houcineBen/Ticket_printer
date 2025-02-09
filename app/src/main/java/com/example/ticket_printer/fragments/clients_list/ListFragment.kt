package com.example.ticket_printer.fragments.clients_list
//
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ticket_printer.R
import com.example.ticket_printer.data.UserViewModel
import com.example.ticket_printer.fragments.ListAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton


class ListFragment : Fragment() {

    private lateinit var mClientViewModel: UserViewModel
    private lateinit var adapter: ListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        // RecyclerView
        adapter = ListAdapter()
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Set the OnItemClickListener
        adapter.setOnItemClickListener { client ->
            // Handle item click and navigate to the next fragment
            val action = ListFragmentDirections.actionListFragmentToClientProfileFragment(client.name, client.phone, client.email, client.lastName, client.id)
            findNavController().navigate(action)
        }

        // ClientViewModel
        mClientViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        mClientViewModel.readAllData.observe(viewLifecycleOwner, Observer { client ->
            adapter.setData(client)
        })

        // SearchView
        val searchView = view.findViewById<SearchView>(R.id.search_view)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    filterList(newText)
                }
                return true
            }
        })



        return view
    }

    private fun filterList(query: String) {
        mClientViewModel.readAllData.observe(viewLifecycleOwner, Observer { clients ->
            val filteredList = clients.filter { client ->
                client.name.contains(query, true) ||
                        client.phone.contains(query, true) ||
                        client.email.contains(query, true)
            }
            adapter.setData(filteredList)
        })
    }
}
