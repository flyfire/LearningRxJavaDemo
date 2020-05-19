package com.solarexsoft.learningrxjavademo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlin.math.log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
    }
    class MainEvent
    lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        SolarexRxBus.instance().toFlowable(MainEvent::class.java).subscribe{
            Log.d(TAG, "event $it")
        }
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        mainViewModel.currentQuestion.observe(this, Observer {
            Log.d(TAG, "current question = $it")
        })
        mainViewModel.mockQuestion()
    }
}
