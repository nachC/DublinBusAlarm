package com.nachc.dba.repository

import androidx.lifecycle.MutableLiveData
import com.nachc.dba.firebase.FirebaseProvider
import com.nachc.dba.models.Trip
import io.reactivex.rxjava3.core.Single

class RouteRepository {

    val firebaseProvider = FirebaseProvider()

    val trips by lazy { MutableLiveData<List<Trip>>() }
    val dataFetched by lazy { MutableLiveData<Boolean>() }

    fun getRouteData(routeID: String): Single<List<Trip>> {
        return firebaseProvider.getRouteData(routeID)
    }
}