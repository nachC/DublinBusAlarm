package com.nachc.dba.routelistscreen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nachc.dba.models.Trip

class RouteListScreenViewModel : ViewModel() {

    val trips by lazy { MutableLiveData<List<Trip>>() }

    fun getTrips() {
        val t1 = Trip("0","origin", "destination", null, null)
        val t2 = Trip("1","origin", "destination", null, null)
        val t3 = Trip("2","origin", "destination", null, null)
        val t4 = Trip("3","origin", "destination", null, null)

        val tripList = arrayListOf(t1, t2, t3, t4)

        trips.value = tripList
    }
}