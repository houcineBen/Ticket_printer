package com.example.ticket_printer.data

import androidx.lifecycle.LiveData

class ClientRepo(private val clientDao: ClientDao) {

    val readAllData: LiveData<List<Client>> = clientDao.readAllData()
//    val allDataList: List<Client> = clientDao.allDataList()

    fun addClient(client: Client): Long {
        return clientDao.addClient(client) // Returns the row ID
    }

    fun getClientsByPhone(phone: String): LiveData<List<Client>> {
        return clientDao.getClientsByPhone(phone)
    }

    fun getClientsByName(name: String): LiveData<List<Client>> {
        return clientDao.getClientsByName(name)
    }

    fun getClientsByLastName(lastName: String): LiveData<List<Client>> {
        return clientDao.getClientsByLastName(lastName)
    }

    fun addVisit(visitHistory: VisitHistory) {
        clientDao.addVisit(visitHistory)
    }

    fun getLastThreeVisitsSync(clientId: Long): List<VisitHistory> {
        return clientDao.getLastThreeVisitsSync(clientId)
    }

    fun getLastVisitSync(clientId: Long): VisitHistory {
        return clientDao.getLastVisitSync(clientId)
    }

    fun getClientCountSync(): Int {
        return clientDao.getClientCountSync()
    }

    fun getClientIdByPhone(phone: String): Long? {
        return clientDao.getClientIdByPhone(phone)
    }

    fun getClientNameByPhone(phone: String): String? {
        return clientDao.getClientNameByPhone(phone)
    }

    fun getClientLastNameByPhone(phone: String): String? {
        return clientDao.getClientLastNameByPhone(phone)
    }

    fun getClientEmailByPhone(phone: String): String? {
        return clientDao.getClientEmailByPhone(phone)
    }

    fun getClientsWithBikes(): LiveData<List<Client>>? {
        return clientDao.getClientsWithBikes()
    }

    fun updateHasBike(clientId: Long, hasBike: Boolean) {
        return clientDao.updateHasBike(clientId, hasBike)
    }






}