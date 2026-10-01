package com.example.blocodenotas.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "notas")
data class Nota(
    @PrimaryKey(autoGenerate = true) val id : Int = 0,
    val titulo : String,
    val conteudo : String,
    val fixada: Boolean = false,
    @ColumnInfo (name = "criada_em") val criadaEm : Long,
    @ColumnInfo (name = "atualizada_em") val atualizadaEm : Long
)

