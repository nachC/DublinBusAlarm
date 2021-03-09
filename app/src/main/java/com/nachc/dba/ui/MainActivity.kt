package com.nachc.dba.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.webkit.WebViewFragment
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.preference.PreferenceManager
import com.nachc.dba.R
import com.nachc.dba.googlemaps.MapsFragmentDirections
import com.nachc.dba.util.stopLocationService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private lateinit var sharedPref: SharedPreferences
    private val APP_INTRO_KEY = "SHOWN_INTRO"

    private lateinit var navController: NavController
    private val CHANNEL_ID = "alarm_channel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val shownIntro = sharedPref.getBoolean(APP_INTRO_KEY, false)

        if (!shownIntro) {
            //sharedPref.edit().putBoolean(APP_INTRO_KEY, true).apply()
            val appIntroIntent = Intent(applicationContext, AppIntroActivity::class.java)
            startActivityForResult(appIntroIntent, 101)
        }

        setContentView(R.layout.activity_main)

        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController)

        // create notification channel to handle the alarm notifications
        createNotificationChannel()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent!!.hasExtra("dismiss")) {
            findNavController(R.id.mapsFragment).navigate(MapsFragmentDirections.actionMapsToMainScreen())
        }
    }

    override fun onBackPressed() {
        Log.i(TAG, "onBackPressed")
        val navhostfragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        Log.i(TAG, navhostfragment!!.childFragmentManager.fragments[0].id.toString())
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