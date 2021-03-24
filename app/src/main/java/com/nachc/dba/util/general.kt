package com.nachc.dba.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.*
import android.provider.Settings
import android.webkit.WebView
import androidx.core.app.ActivityCompat.startActivityForResult

fun isInternetAvailable(context: Context, callback: (Boolean) -> Unit) {
    // Check for network connectivity. We'll allow the search functionality only if a connection is available
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val request = NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build()
    cm.registerNetworkCallback(request, object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            callback(true)
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            callback(false)
        }
    })
}

// method called when clicking the permission button
// we send the user to the OS settings screen to set location permission manually
fun openPermissionSettings(activity: Activity, requestCode: Int) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts("package", activity.packageName, null)
    intent.data = uri
    // we'll handle the result on onActivityResult
    startActivityForResult(activity, intent, requestCode, null)
}