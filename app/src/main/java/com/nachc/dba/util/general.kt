package com.nachc.dba.util

import android.content.Context
import android.net.*

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