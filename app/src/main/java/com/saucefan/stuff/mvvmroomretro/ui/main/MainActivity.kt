package com.saucefan.stuff.mvvmroomretro.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.saucefan.stuff.mvvmroomretro.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
    }

}
