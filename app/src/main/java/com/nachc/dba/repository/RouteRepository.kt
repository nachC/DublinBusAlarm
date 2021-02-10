package com.nachc.dba.repository

import com.nachc.dba.di.DaggerRouteComponent
import com.nachc.dba.firebase.FirebaseProvider
import com.nachc.dba.models.Trip
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class RouteRepository {

    @Inject
    lateinit var firebaseProvider: FirebaseProvider

    init {
        DaggerRouteComponent.create().inject(this)
    }

    fun getRouteData(routeID: String): Single<List<Trip>> {
        return firebaseProvider.getRouteData(routeID)
    }
}