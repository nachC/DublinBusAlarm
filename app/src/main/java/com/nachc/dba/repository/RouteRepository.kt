package com.nachc.dba.repository

import com.nachc.dba.firebase.FirebaseProvider
import com.nachc.dba.models.Trip
import io.reactivex.rxjava3.core.Single

class RouteRepository {

    val firebaseProvider = FirebaseProvider()

    fun getRouteData(routeID: String): Single<List<Trip>> {
        return firebaseProvider.getRouteData(routeID)
    }
}