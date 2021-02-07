package com.nachc.dba.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat


class LocationService: Service() {

    private val TAG = "LocationService"
    private val LOCATION_SERVICE_CHANNEL_ID = "location_service_channel"

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        val name = "Location Service"
        val descriptionText = "channel for location service notification"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(LOCATION_SERVICE_CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        val notification: Notification = NotificationCompat.Builder(this, LOCATION_SERVICE_CHANNEL_ID).build()
        startForeground(1, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "onStartCommand called")
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}