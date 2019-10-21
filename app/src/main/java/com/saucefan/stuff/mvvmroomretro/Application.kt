package com.saucefan.stuff.mvvmroomretro

import android.app.Application
import android.content.Context
import android.graphics.Typeface

class MyApp: Application() {

    override fun onCreate() {
        super.onCreate()
        //instance = this
    }

    //I don't think this actually works, was trying to find a way to set data at application level so as to make it easily acessable by differing
    //unrelated classes -- something like dagger is the way to do this but I don't have any idea how dagger works atm.
    companion object {
        lateinit var instance: MyApp
            private set
    }

    init {
        instance = this
    }



}