package com.example.ticket_printer.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "Client_data", indices = [Index(value = ["phone"], unique = true)])
data class Client(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    val phone: String
)