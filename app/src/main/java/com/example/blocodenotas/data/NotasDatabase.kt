package com.example.blocodenotas.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Nota::class], version = 1, exportSchema = false)
abstract class NotasDatabase : RoomDatabase() {

    abstract fun notaDao() : NotaDao

    companion object{
        @Volatile
        private var instancia : NotasDatabase? = null

        fun obter(context : Context) : NotasDatabase {
            return instancia ?: synchronized(this){
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    NotasDatabase::class.java,
                    "notas.db"
                ).build().also{ instancia = it}
            }
        }
    }
}