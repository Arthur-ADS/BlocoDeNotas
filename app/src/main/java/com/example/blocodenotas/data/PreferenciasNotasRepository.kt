package com.example.blocodenotas.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import java.io.IOException
import kotlinx.coroutines.flow.catch
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.map

private val Context.dataStore : DataStore<Preferences> by preferencesDataStore(
    name = "preferencias_notas"
)

class PreferenciasNotasRepository (private val context : Context) {

    private object Chaves {
        val ORDENACAO = stringPreferencesKey("ordenacao")
    }

    val ordenacao : Flow<Ordenacao> = context.dataStore.data
        .catch { erro ->
            if (erro is IOException) emit(emptyPreferences()) else throw erro
        }
        .map { prefs ->
            val nome = prefs[Chaves.ORDENACAO]
            Ordenacao.entries.firstOrNull { it.name == nome } ?: Ordenacao.RECENTES
        }

    suspend fun definirOrdenacao(ordenacao : Ordenacao){
        context.dataStore.edit {prefs -> prefs[Chaves.ORDENACAO] = ordenacao.name}
    }
}