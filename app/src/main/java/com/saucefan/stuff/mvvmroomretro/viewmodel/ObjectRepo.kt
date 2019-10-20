package com.saucefan.stuff.mvvmroomretro.viewmodel

import androidx.lifecycle.LiveData
import com.saucefan.stuff.mvvmroomretro.room.RoomDao
import com.saucefan.stuff.mvvmroomretro.room.Userz

class ObjectRepo (private val roomDao: RoomDao){


        suspend fun returnAllUsers(): List<Userz> {
            return roomDao.returnAllUsers()
        }



}