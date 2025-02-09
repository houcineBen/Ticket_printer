//package com.example.ticket_printer.data
//
//import android.content.Context
//import androidx.room.Database
//import androidx.room.Room
//import androidx.room.RoomDatabase
//
//@Database(entities = [Client::class], version = 1, exportSchema = false)
//abstract class ClientDatabase: RoomDatabase() {
//
//    abstract fun ClientDao(): ClientDao
//
//    companion object {
//        @Volatile
//        private var INSTANCE: ClientDatabase? = null
//
//        fun getDatabase(context : Context): ClientDatabase{
//            val tempInstance = INSTANCE
//            if(tempInstance != null){
//                return tempInstance
//            }
//            synchronized(this){
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    ClientDatabase::class.java,
//                    "client_database"
//                ).build()
//                INSTANCE = instance
//                return instance
//
//            }
//        }
//    }
//
//}

package com.example.ticket_printer.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Client::class, VisitHistory::class], version = 7, exportSchema = false)
abstract class ClientDatabase: RoomDatabase() {

    abstract fun ClientDao(): ClientDao

    companion object {
        @Volatile
        private var INSTANCE: ClientDatabase? = null

        fun getDatabase(context : Context): ClientDatabase{
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ClientDatabase::class.java,
                    "client_database"
                ).fallbackToDestructiveMigration() // This will destroy the old database and create a new one
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}