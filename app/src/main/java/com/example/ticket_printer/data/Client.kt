
package com.example.ticket_printer.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "Client_data")
data class Client(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
    val lastName: String,
    val phone: String,
    val email: String,
    val hasBike: Boolean = false
)