package com.saucefan.stuff.mvvmroomretro.viewmodel

import com.saucefan.stuff.mvvmroomretro.model.room.RoomDao
import com.saucefan.stuff.mvvmroomretro.model.room.Userz

class ObjectRepo (private val roomDao: RoomDao){


        suspend fun returnAllUsers(): List<Userz> {
            return roomDao.returnAllUsers()
        }
        suspend fun returnAUser(first:String,last:String):Userz {
            return roomDao.findByName()
        }


}