package com.saucefan.stuff.mvvmroomretro

import android.app.Application
import android.content.Context
import android.graphics.Typeface

class MyApp: Application() {

    override fun onCreate() {
        super.onCreate()
        //instance = this
    }

    companion object {
        lateinit var instance: MyApp
            private set
    }

    init {
        instance = this
    }



}