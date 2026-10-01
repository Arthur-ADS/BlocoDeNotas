package com.example.blocodenotas.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NotaDao {

    @Query("SELECT * FROM notas ORDER BY fixada DESC, atualizada_em DESC")
    fun observarPorRecentes() : Flow<List<Nota>>

    @Query("SELECT * FROM notas ORDER BY fixada DESC, titulo COLLATE NOCASE ASC")
    fun observarPorTitulo() : Flow<List<Nota>>

    @Insert
    suspend fun inserir(nota : Nota)

    @Update
    suspend fun atualizar(nota : Nota)

    @Delete
    suspend fun excluir(nota : Nota)
}