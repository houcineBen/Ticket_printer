package com.example.ticket_printer


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import com.example.ticket_printer.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val customers_item = binding.bottomNavigationView
        customers_item.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_printFragment_to_listFragment)
        }


    }

}
