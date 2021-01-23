package com.nachc.dba.routelistscreen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nachc.dba.models.Trip

class RouteListScreenViewModel : ViewModel() {

    val trips by lazy { MutableLiveData<List<Trip>>() }

    fun getTrips() {
        val t1 = Trip("origin", "destination", null, null)
        val t2 = Trip("origin2", "destination2", null, null)
        val t3 = Trip("origin3", "destination3", null, null)
        val t4 = Trip("origin4", "destination4", null, null)

        val tripList = arrayListOf(t1, t2, t3, t4)

        trips.value = tripList
    }
}