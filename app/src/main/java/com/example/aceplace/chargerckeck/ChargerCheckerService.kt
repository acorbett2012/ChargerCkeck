package com.example.aceplace.chargerckeck

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.os.HandlerCompat.postDelayed
import android.util.Log
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import android.os.AsyncTask
import android.util.JsonReader






class ChargerCheckerService : Service() {

    var isRunning = true

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
        Log.w("Warn", "onBind")
        isRunning = true
    }

    override fun onDestroy() {
        isRunning = false
        Log.w("Warn", "onDestoy")
    }

    private var mHandler: Handler? = null
    // default interval for syncing data
    val DEFAULT_SYNC_INTERVAL = (60 * 1000).toLong()

    // task to be run here
    private val runnableService = object : Runnable {
        override fun run() {
            syncData()
            // Repeat this runnable code block again every ... min
            if (isRunning) {
                mHandler!!.postDelayed(this, DEFAULT_SYNC_INTERVAL)
            }
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // Create the Handler object
//        isRunning = true
        this.mHandler = Handler()
        // Execute a runnable task as soon as possible
        mHandler!!.post(runnableService);

        return START_STICKY
    }



    @Synchronized
    private fun syncData() {
        Log.w("Warn", "syncData")
        // call your rest service here
        AsyncTask.execute {
            // All your networking logic
            // should be here


            val githubEndpoint = URL("https://cors-anywhere.herokuapp.com/https://network.semaconnect.com/get_data.php")
            val myConnection = githubEndpoint.openConnection() as HttpsURLConnection
            myConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            myConnection.setRequestMethod("POST");
            myConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36");
            myConnection.setRequestProperty("Origin", "space");
            val data = "action=mapLocationDetails&div_id=1499&user_id=35282"
            // Enable writing
            myConnection.doOutput = true;
            // Write the data
            myConnection.outputStream.write(data.toByteArray());


            if (myConnection.getResponseCode() == 200) {
                Log.w("Warn", "Call Succeeded")
                // Success
                // Further processing here
                val responseBody = myConnection.inputStream
                val responseBodyReader = InputStreamReader(responseBody, "UTF-8")
                val jsonReader = JsonReader(responseBodyReader)
                Log.w("Warn", jsonReader.toString())

                jsonReader.beginObject() // Start processing the JSON object
                while (jsonReader.hasNext()) { // Loop through all keys
                    val key = jsonReader.nextName() // Fetch the next key
                    if (key == "aaData") { // Check if desired key
                        // Fetch the value as a String

                        Log.w("Warn", "available found")
                        Log.w("Warn", key)
                        jsonReader.beginObject()
                        while (jsonReader.hasNext()) {
                            val key2 = jsonReader.nextName() // Fetch the next key
                            if (key2 == "stations") { // Check if desired key
                                Log.w("Warn", "STATION FOUND")
                                jsonReader.beginArray()
                                jsonReader.beginObject()
                                while (jsonReader.hasNext()) {
                                    val key3 = jsonReader.nextName() // Fetch the next key
                                    if (key3 == "available") { // Check if desired key
                                        val value = jsonReader.nextString()
                                        Log.w("Warn", key3)

                                        Log.w("Warn", value)

                                        if(value != "0"){
                                            notifyOpenCharger()
                                        }

                                    }else {
                                        jsonReader.skipValue()
                                    }
                                }

                            } else {
                                jsonReader.skipValue()
                            }
                        }

//                        val value = jsonReader.nextString()

                        // Do something with the value
                        // ...

                        break // Break out of the loop
                    } else {
                        Log.w("Warn", key)
//                        Log.w("WARN", jsonReader.nextString())
                        jsonReader.skipValue() // Skip values of other keys
                    }
                }






                jsonReader.close()
            } else {
                Log.w("Warn", "Call Failed")
                Log.w("Warn", myConnection.responseCode.toString())
                Log.w("Warn", myConnection.responseMessage)
                // Error handling code goes here
            }

            myConnection.disconnect()
        }
    }



    fun notifyOpenCharger() {
        val mBuilder = NotificationCompat.Builder(this, "whatevver")
                .setSmallIcon(R.drawable.charger_open_notification)
                .setContentTitle("Charger Open")
                .setContentText("There is an opening at the charger")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setDefaults(NotificationCompat.DEFAULT_VIBRATE)

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(31415, mBuilder.build())
    }

}
