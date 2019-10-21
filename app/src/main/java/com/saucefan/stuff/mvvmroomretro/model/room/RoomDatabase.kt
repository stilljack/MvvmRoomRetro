package com.saucefan.stuff.mvvmroomretro.model.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.random.Random

@Database(entities = [Userz::class], version = 3, exportSchema = false)
//@TypeConverters(Converters::class)
abstract class ObjectDatabase : RoomDatabase() {
    abstract fun RoomDao(): RoomDao

    companion object {

        //so we only get a single total instance of the database
        @Volatile
        private var INSTANCE: ObjectDatabase? = null

        fun getInstance(context: Context): ObjectDatabase? {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    ObjectDatabase::class.java, "user_database")
                    .fallbackToDestructiveMigration() // when version increments, it migrates (deletes db and creates new) - else it crashes
                   // this should only fire if the database is empty/first used and will fill it via the EntryDataBaseCallBack Method
                    .addCallback(EntryDatabaseCallback(GlobalScope))
                    .build()
                INSTANCE=instance
                return instance

            }
        }
        fun destroyInstance() {
            INSTANCE = null
        }

    }
    private class EntryDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    var roomDao = database.RoomDao()
                    //  database.clearAllTables()
                    populateDatabase(roomDao)



                }
            }
        }
        suspend fun populateDatabase(roomDao: RoomDao) {

        //   EntryMockData.entryList.forEach {
                roomDao.insertAll(Userz(0,"first+${Random.nextInt(20)}","${Random.nextInt(20)}"))
       //     }

        }
    }

}