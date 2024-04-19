package com.example.ticket_printer.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface ClientDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addClient(client: Client)

    @Query("SELECT * FROM client_data ORDER BY id ASC")
    fun readAllData(): LiveData<List<Client>>

//    @Query("SELECT * FROM client_data ORDER BY id ASC")
//    fun allDataList(): List<Client>

    @Query("SELECT * FROM client_data WHERE phone LIKE :phone||'%'")
    fun getClientByPhonePrefix(phone: String): LiveData<List<Client>>

    @Query("SELECT * FROM client_data WHERE name LIKE :name||'%'")
    fun getClientByName(name: String): LiveData<List<Client>>

}