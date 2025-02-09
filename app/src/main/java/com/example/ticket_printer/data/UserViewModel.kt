package com.example.ticket_printer.data

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ClientRepo
    val clientsLiveData = MediatorLiveData<List<Client>>()
    private var currentSource: LiveData<List<Client>>? = null

    val readAllData: LiveData<List<Client>>
    fun addClient(client: Client): Long = repository.addClient(client)

    init {
        val clientDao = ClientDatabase.getDatabase(application).ClientDao()
        repository = ClientRepo(clientDao)
        readAllData = repository.readAllData
    }

    fun searchByPhone(phonePrefix: String) {
        currentSource?.let { clientsLiveData.removeSource(it) }
        val newSource = repository.getClientsByPhone(phonePrefix)
        currentSource = newSource
        clientsLiveData.addSource(newSource) { clients ->
            clientsLiveData.value = clients
        }
    }


    fun searchByName(name: String) {
        currentSource?.let { clientsLiveData.removeSource(it) }
        val newSource = repository.getClientsByName(name)
        currentSource = newSource
        clientsLiveData.addSource(newSource) { clients ->
            clientsLiveData.value = clients
        }
    }

    fun searchByLastName(lastName: String) {
        currentSource?.let { clientsLiveData.removeSource(it) }
        val newSource = repository.getClientsByLastName(lastName)
        currentSource = newSource
        clientsLiveData.addSource(newSource) { clients ->
            clientsLiveData.value = clients
        }
    }



    // Existing methods
    fun addVisit(visitHistory: VisitHistory) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addVisit(visitHistory)
        }
    }

    fun getLastThreeVisitsSync(clientId: Long): List<VisitHistory> =
        repository.getLastThreeVisitsSync(clientId)

    fun getLastVisitSync(clientId: Long): VisitHistory =
        repository.getLastVisitSync(clientId)

    fun getClientIdByPhone(phone: String): Long? =
        repository.getClientIdByPhone(phone)


    fun getClientNameByPhone(phone: String): String? =
        repository.getClientNameByPhone(phone)


    fun getClientLastNameByPhone(phone: String): String? =
        repository.getClientLastNameByPhone(phone)

    fun getClientEmailByPhone(phone:String) : String? =
        repository.getClientEmailByPhone(phone)

    fun clientsWithBikes(): LiveData<List<Client>>? =
        repository.getClientsWithBikes()

    fun updateHasBike(clientId: Long, hasBike: Boolean) =
        repository.updateHasBike(clientId, hasBike)

}