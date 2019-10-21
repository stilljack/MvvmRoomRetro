package com.saucefan.stuff.mvvmroomretro.ui.main

import android.app.ProgressDialog.show
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.saucefan.stuff.mvvmroomretro.MyApp
import com.saucefan.stuff.mvvmroomretro.R
import com.saucefan.stuff.mvvmroomretro.room.Userz
import com.saucefan.stuff.mvvmroomretro.viewmodel.LiveDataVMFactory
import com.saucefan.stuff.mvvmroomretro.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.coroutines.*
import okhttp3.internal.platform.Platform.Companion.get
import kotlin.random.Random


class MainFragment : Fragment() {

    var job = Job()
    val ioScope = CoroutineScope(Dispatchers.IO + job)
    val uiScope = CoroutineScope(Dispatchers.Main + job)
    lateinit var ourView: TextView

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

        viewModel.allUsers.observe(this,nameObserver)

        viewModel.allUsers()
        ourView = view2
        btn1.setOnClickListener {
            //just so it's a repeatable action, since var job is in the scope of application, code continues,
            // blah blah blah new Job() basically
            job = Job()

        //   doAsycThing() if we just want to get it done
            // getAnAsyncObjectBack() lets us easily set as loading, or if there's an error, etc
            //first hide other textview
            view1.visibility=View.GONE
            view3.visibility=View.GONE
            //then we set deffered to the result of the getAnAsyncObject()
            val deferred = getAnAsyncObjectBack()
            //we can immediately set a loading message or what have you
            if(deferred.isActive)  {
                ourView.text="loading"
            }
            else if(deferred.isCancelled){
                ourView.text="cancelled"
            }
            // there's likely a better way to do this, but it does work... until the delay goes off and we getthe result back,
            //we see the loading message, then invokeOnCompletion is triggered

            //we can also cancel this job with button 2
            deferred.invokeOnCompletion {
                uiScope.launch {
                    Toast.makeText(view!!.context, deferred.await().toString(),Toast.LENGTH_SHORT).show()
                    ourView.text=deferred.await().firstName
                }
            }
        }
        btn2.setOnClickListener {
            if(job.isActive){
                ourView.text="canceling"
            }
            job.cancel()
            if(job.isCancelled){
                ourView.text="the job is canceled"
            }

           // doAsycThingInOneMethod()
        }
    }

    fun doAsycThing()  {
        ioScope.async {
            val returned = viewModel.findUser("first", "").await()
callback(returned)
    }
    }

fun callback(userz: Userz) {
    uiScope.launch {
        Toast.makeText(context,"${Random.nextInt(10)} $userz" , Toast.LENGTH_SHORT).show()
        ourView.text = "${Random.nextInt(10)} $userz"
    }
}


fun doAsycThingInOneMethod()  {
    ioScope.async {
        val returned = viewModel.findUser("first", "").await()
        uiScope.launch {
            Toast.makeText(context, "${Random.nextInt(10)}  $returned", Toast.LENGTH_SHORT).show()
            ourView.text = "${Random.nextInt(10)} $returned"
        }
    }
}

    fun getAnAsyncObjectBack():Deferred<Userz> =
        ioScope.async {
            delay(3000)
             viewModel.findUser("first", "").await()

        }

        }