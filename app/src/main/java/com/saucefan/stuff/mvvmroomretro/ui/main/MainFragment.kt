package com.saucefan.stuff.mvvmroomretro.ui.main

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
import com.saucefan.stuff.mvvmroomretro.model.room.Userz
import com.saucefan.stuff.mvvmroomretro.viewmodel.LiveDataVMFactory
import com.saucefan.stuff.mvvmroomretro.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.coroutines.*
import java.lang.Thread.sleep
import kotlin.random.Random


class MainFragment : Fragment() {

    var job = Job()
    var ioScope = CoroutineScope(Dispatchers.IO + job)
    var uiScope = CoroutineScope(Dispatchers.Main + job)
    lateinit var ourView: TextView
    private lateinit var viewModel: MainViewModel

    companion object {
        fun newInstance() = MainFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //viewmodel initialization
        viewModel = ViewModelProviders.of(this, LiveDataVMFactory(MyApp.instance))
            .get(MainViewModel::class.java)
        val nameObserver = Observer<List<Userz>> { newName ->
            view1.text = newName[0].toString()
        }
        viewModel.allUsers.observe(this, nameObserver)
        viewModel.allUsers()



        //grab the textview to change
        ourView = view2
        //turn of cancel button til there's something to cancel
        btn2.isEnabled = false


        btn1.setOnClickListener {
            //just so it's a repeatable action, we reinitialize job if job has already been completed or canceled, if
            // job.isActive, btn1 should not be enabled so we should not arrive here,
            // blah blah blah new Job() basically -- then we just have to join it to the scopes, and we have
            // an action that is cancelable and repeatable.

            if (job.isCancelled || job.isCompleted) {
                job = Job()
                ioScope = CoroutineScope(Dispatchers.IO + job)
                uiScope = CoroutineScope(Dispatchers.Main + job)
            }
            //turn of request button and turn on cancel button
            btn1.isEnabled = false
            btn2.isEnabled = true

            //   doAsycThing() if we just want to get it done
            // getAnAsyncObjectBack() lets us easily set as loading, or if there's an error, etc
            //first hide other textview
            view1.visibility = View.GONE
            view3.visibility = View.GONE

            //then we set deffered to the result of the getAnAsyncObject()
            val deferred = getAnAsyncObjectBack()

            //we can immediately set a loading message or what have you
            if (job.isActive) {
                ourView.text = "loading"
            } else if (job.isCancelled) {
                ourView.text = "cancelled"
            } else if (job.isCompleted) {
                val final = deferred.getCompleted()
                ourView.text = final.toString()
            }
            // there's likely a better way to do this, but it does work... until the delay goes off and we get the result back,
            //we see the loading message, then invokeOnCompletion is triggered -- Please Note: the job itself is not completed
            //until we call job.complete() -- hence we need to check if deferred is completed, not the job itself.

            //we can also cancel this job with button 2
            deferred.invokeOnCompletion {

                uiScope.launch {
                    Toast.makeText(view!!.context, deferred.await().toString(), Toast.LENGTH_SHORT)
                        .show()
                    ourView.text = deferred.await().firstName
                    btn1.isEnabled = true
                    btn2.isEnabled = false

                    //we call job.complete() to set job.isCompleted so we can check if this job is done,
                    //and canceling is useless and displaying an indication that the completed job
                    // is canceled to the user would be
                    //confusing AND stupid -- additionally this lets us know that we need to make a new job if there is another request by
                    // the same method

                    job.complete()
                }
            }
        }
        btn2.setOnClickListener {
            //if the job is not completed, we cancel it
            if (!job.isCompleted) {
                if (job.isActive) {
                    //setting the text view doesn't help much as canceling a job is basically instant
                    //so we could pop a toast or snackbar to indicate to user that canceling is "processing"
                    ourView.text = "canceling"
                    Toast.makeText(it.context, "Canceling", Toast.LENGTH_LONG).show()
                }
                //this is all it takes to cancel and ditch all the implications as the invokeOnCompletion Lambda will never fire.

                job.cancel()


                if (job.isCancelled) {
                    ourView.text = "the job is canceled"
                    btn1.isEnabled = true
                    btn2.isEnabled = false
                }
            }


        }
    }

    //theres very little to this bit, of course some of what happens with the onclick Listerners could be either moved to other methods
    // or to this one.
    fun getAnAsyncObjectBack(): Deferred<Userz> =
        ioScope.async {
            //so there's time to actually cancel things manually or observe loading message
            //we delay 3 seconds
            delay(3000)
            viewModel.findUser("first", "").await()

        }

    // these next two methods are in a call back style where the callback only fires after the first method completes
    // unfortunately we still have to make sure we run the secondary code from Dispatchers.Main if we wish to alter the UI or
    //otherwise do anything with context, to alter LiveData or similar, this would not be necessary
    fun doAsycThing() {
        ioScope.async {
            val returned = viewModel.findUser("first", "").await()
            callback(returned)
        }
    }
    fun callback(userz: Userz) {
        uiScope.launch {
            Toast.makeText(context, "${Random.nextInt(10)} $userz", Toast.LENGTH_SHORT).show()
            ourView.text = "${Random.nextInt(10)} $userz"
        }
    }

// identical function to the first two methods without a call back style.
    fun doAsycThingInOneMethod() {
        ioScope.async {
            val returned = viewModel.findUser("first", "").await()
            uiScope.launch {
                Toast.makeText(context, "${Random.nextInt(10)}  $returned", Toast.LENGTH_SHORT)
                    .show()
                ourView.text = "${Random.nextInt(10)} $returned"
            }
        }
    }




}