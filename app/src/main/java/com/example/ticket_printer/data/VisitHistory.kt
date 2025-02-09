package com.example.ticket_printer.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "visit_history",
    foreignKeys = [ForeignKey(
        entity = Client::class,
        parentColumns = ["id"],
        childColumns = ["clientId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class VisitHistory(
    @PrimaryKey(autoGenerate = true) val id : Int = 0,
    val clientId: Long, // Foreign key to Client
    val dateTime: String, // Date and time of the visit
    val bikeFixDetails: String // Details of the bike fixings
)