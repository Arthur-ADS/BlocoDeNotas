package com.example.blocodenotas

import com.example.blocodenotas.data.AppContainer
import android.app.Application
class NotasApp : Application() {

    lateinit var container : AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}