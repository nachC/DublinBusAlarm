package com.nachc.dba.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.nachc.dba.R

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private val CHANNEL_ID = "alarm_channel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController)

        // create notification channel to handle the alarm notifications
        createNotificationChannel()

    }

    override fun onSupportNavigateUp(): Boolean {
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