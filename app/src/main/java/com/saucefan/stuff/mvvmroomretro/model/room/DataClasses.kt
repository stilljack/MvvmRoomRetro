package com.saucefan.stuff.mvvmroomretro.model.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "userz")
data class Userz(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name ="uid")  var uid: Int = 0,
    @ColumnInfo(name = "first_name") var firstName: String,
    @ColumnInfo(name = "last_name") var lastName: String
  //  @Ignore var user: Userz? = null

)