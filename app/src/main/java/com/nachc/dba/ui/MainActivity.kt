package com.nachc.dba.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.nachc.dba.R
import com.nachc.dba.googlemaps.MapsFragmentDirections
import com.nachc.dba.ui.appintro.AppIntroActivity
import com.nachc.dba.util.stopLocationService

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private lateinit var navController: NavController
    private val CHANNEL_ID = "alarm_channel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appIntroIntent = Intent(applicationContext, AppIntroActivity::class.java)
        startActivity(appIntroIntent)

        setContentView(R.layout.activity_main)

        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController)

        // create notification channel to handle the alarm notifications
        createNotificationChannel()

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent!!.hasExtra("dismiss")) {
            findNavController(R.id.mapsFragment).navigate(MapsFragmentDirections.actionMapsToSearchScreen())
        }
    }

    override fun onBackPressed() {
        Log.i(TAG, "onBackPressed")
        stopLocationService(this)
        super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        Log.i(TAG, "onSupportNavigateUp")
        stopLocationService(this)
        return NavigationUI.navigateUp(navController, null)
    }

    fun createNotificationChannel() {
        val name: CharSequence = getString(R.string.alarm_channel_name)
        val description = getString(R.string.alarm_channel_description)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val alarmChannel = NotificationChannel(CHANNEL_ID, name, importance)
        alarmChannel.description = description
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        val notificationManager = getSystemService(
            NotificationManager::class.java
        )
        notificationManager.createNotificationChannel(alarmChannel)
    }
}