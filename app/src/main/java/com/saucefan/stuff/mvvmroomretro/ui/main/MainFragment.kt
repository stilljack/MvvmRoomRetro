package com.saucefan.stuff.mvvmroomretro.ui.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.saucefan.stuff.mvvmroomretro.MyApp
import com.saucefan.stuff.mvvmroomretro.R
import com.saucefan.stuff.mvvmroomretro.room.Userz
import com.saucefan.stuff.mvvmroomretro.viewmodel.LiveDataVMFactory
import com.saucefan.stuff.mvvmroomretro.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.main_fragment.*

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

   private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this, LiveDataVMFactory(MyApp.instance)).get(MainViewModel::class.java)

        val nameObserver = Observer<List<Userz>> { newName ->
            view1.text = newName[0].toString()
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.allUsers.observe(this,nameObserver)

        viewModel.allUsers()
    }

}
