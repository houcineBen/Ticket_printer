package com.example.ticket_printer.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface ClientDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addClient(client: Client): Long // Returns the row ID of the inserted client

    @Query("SELECT * FROM client_data ORDER BY id ASC")
    fun readAllData(): LiveData<List<Client>>

//    @Query("SELECT * FROM client_data ORDER BY id ASC")
//    fun allDataList(): List<Client>

    @Query("SELECT * FROM client_data WHERE phone LIKE :phone||'%'")
    fun getClientsByPhone(phone: String): LiveData<List<Client>>

    @Query("SELECT * FROM client_data WHERE lastName LIKE :lastName||'%'")
    fun getClientsByLastName(lastName: String): LiveData<List<Client>>

    @Query("SELECT * FROM client_data WHERE name LIKE :name||'%'")
    fun getClientsByName(name: String): LiveData<List<Client>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addVisit(visitHistory: VisitHistory)

    @Query("SELECT * FROM visit_history WHERE clientId = :clientId ORDER BY dateTime DESC LIMIT 3")
    fun getLastThreeVisitsSync(clientId: Long): List<VisitHistory>

    @Query("SELECT * FROM visit_history WHERE clientId = :clientId ORDER BY dateTime DESC LIMIT 1")
    fun getLastVisitSync(clientId: Long): VisitHistory

    @Query("SELECT COUNT(*) FROM client_data")
    fun getClientCountSync(): Int

    @Query("SELECT id FROM client_data WHERE phone = :phone LIMIT 1")
    fun getClientIdByPhone(phone: String): Long?

    @Query("SELECT name FROM client_data WHERE phone = :phone LIMIT 1")
    fun getClientNameByPhone(phone: String): String?

    @Query("SELECT lastName FROM client_data WHERE phone = :phone LIMIT 1")
    fun getClientLastNameByPhone(phone: String): String?

    @Query("SELECT email FROM client_data WHERE phone = :phone LIMIT 1")
    fun getClientEmailByPhone(phone: String): String

    @Query("SELECT * FROM Client_data WHERE hasBike = 1")
    fun getClientsWithBikes(): LiveData<List<Client>>?

    @Query("UPDATE Client_data SET hasBike = :hasBike WHERE id = :clientId")
    fun updateHasBike(clientId: Long, hasBike: Boolean)




}