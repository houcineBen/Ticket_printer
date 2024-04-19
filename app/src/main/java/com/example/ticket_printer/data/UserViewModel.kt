package com.example.ticket_printer.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application): AndroidViewModel(application) {

    val readAllData: LiveData<List<Client>>
//    val allDataList: List<Client>
    private val repository: ClientRepo

    init {
        val clientDao = ClientDatabase.getDatabase(application).ClientDao()
        repository = ClientRepo(clientDao)
        readAllData = repository.readAllData
//        allDataList = repository.allDataList
    }

    fun addClient(client: Client) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addClient(client)
        }
    }

    fun getClientByPhonePrefix(phonePrefix: String): LiveData<List<Client>> {
        return repository.getClientByPhonePrefix(phonePrefix)
    }

    fun getClientByName(name: String): LiveData<List<Client>> {
        return repository.getClientByName(name)
    }
}