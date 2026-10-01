package com.example.blocodenotas.data

import android.content.Context

class AppContainer (context : Context) {

    private val appContext = context.applicationContext

    val notasRepository : NotasRepository by lazy {
        NotasRepository(NotasDatabase.obter(appContext).notaDao())
    }

    val preferenciasRepository : PreferenciasNotasRepository by lazy {
        PreferenciasNotasRepository(appContext)
    }
}