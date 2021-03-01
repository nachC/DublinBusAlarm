package com.nachc.dba.util

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import com.nachc.dba.services.LocationService

fun startLocationService(context: Context) {
    Intent(context, LocationService::class.java).also { intent ->
        context.startService(intent)
    }
}

fun stopLocationService(context: Context) {
    val stopLocationServiceIntent = Intent(context, LocationService::class.java)
    context.stopService(stopLocationServiceIntent)
}

