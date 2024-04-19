package com.example.ticket_printer.data

import androidx.lifecycle.LiveData

class ClientRepo(private val clientDao: ClientDao) {

    val readAllData: LiveData<List<Client>> = clientDao.readAllData()
//    val allDataList: List<Client> = clientDao.allDataList()

    fun addClient(client: Client) {
        clientDao.addClient(client)
    }

    fun getClientByPhonePrefix(phone: String): LiveData<List<Client>> {
        return clientDao.getClientByPhonePrefix(phone)
    }

    fun getClientByName(name: String): LiveData<List<Client>> {
        return clientDao.getClientByName(name)
    }

}