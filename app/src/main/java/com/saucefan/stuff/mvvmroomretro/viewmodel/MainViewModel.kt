package com.saucefan.stuff.mvvmroomretro.viewmodel

import android.app.Application
import android.provider.Contacts
import androidx.lifecycle.*
import com.saucefan.stuff.mvvmroomretro.room.ObjectDatabase
import com.saucefan.stuff.mvvmroomretro.room.RoomDao
import com.saucefan.stuff.mvvmroomretro.room.Userz
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.reflect.Field
import java.util.HashMap


class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ObjectRepo


    private val _mUsers = MediatorLiveData<List<Userz>>()

    val allUsers: LiveData<List<Userz>> = _mUsers  // 1


    fun allUsers() = viewModelScope.launch {

        _mUsers.postValue(repository.returnAllUsers())
    }
    fun findUser(first:String,last:String): Deferred<Userz> = viewModelScope.async {

            repository.returnAUser(first, last)

        }

    init {
        val roomDao = ObjectDatabase.getInstance(getApplication())?.RoomDao() as RoomDao
        repository = ObjectRepo(roomDao)
    }
}

/*
suspend fun fillAllUsers():LiveData<List<Userz>>{
    val LdLU  =  viewModelScope.async {
        repository.returnAllUsers()
    }
    LdLU.await()
    return LdLU.await()

}*/



/*suspend fun fillAllUsers():LiveData<List<Userz>>{
    val LdLU  =  viewModelScope.async {
        repository.returnAllUsers()
    }
    LdLU.await()
 return LdLU.await()

    }*/

class LiveDataVMFactory (application: Application) : ViewModelProvider.Factory {
    val application = application
    //  private val dataSource = DefaultDataSource(Dispatchers.IO)
    val hashMapViewModel = HashMap<String, ViewModel>()

    fun addViewModel(key: String, viewModel: ViewModel) {
        hashMapViewModel.put(key, viewModel)
    }

    fun getViewModel(key: String): ViewModel? {
        return hashMapViewModel[key]
    }


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            val key = "SharedViewModel"
            if (hashMapViewModel.containsKey(key)) {
                return getViewModel(key) as T
            } else {
                addViewModel(key, MainViewModel(application))
                return getViewModel(key) as T
            }
        }
        throw IllegalArgumentException("Unknown ViewModel class")
        /*     return SharedViewModel() as T

    }*/
    }
}