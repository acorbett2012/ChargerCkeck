package com.example.aceplace.chargerckeck

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationCompat.DEFAULT_VIBRATE
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import android.content.Intent




class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    fun startChecking(view: View){
        // stuff
        Log.w("Warn", "StartChecking")
        val startServiceIntent = Intent(this@MainActivity, ChargerCheckerService::class.java)
        startService(startServiceIntent)
    }

    fun stopChecking(view: View){
        // stuff
        Log.w("Warn", "StopChecking")
        val stopServiceIntent = Intent(this@MainActivity, ChargerCheckerService::class.java)
        stopService(stopServiceIntent)
    }
}
