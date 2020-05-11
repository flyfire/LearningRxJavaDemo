package com.solarexsoft.learningrxjavademo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlin.math.log
import androidx.lifecycle.Observer

class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName
    class MainEvent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        SolarexRxBus.instance().toFlowable(MainEvent::class.java).subscribe{
            Log.d(TAG, "event $it")
        }
    }
}
