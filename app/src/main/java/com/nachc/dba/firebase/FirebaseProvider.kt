package com.nachc.dba.firebase

import android.util.Log
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.nachc.dba.models.CoordinatePoint
import com.nachc.dba.models.Stop
import com.nachc.dba.models.Trip
import io.reactivex.rxjava3.core.Single

class FirebaseProvider {

    /**
     * TODO:
     *  - map shape and stop_sequence from FB to model
     * */

    private val TAG = "FirebaseProvider"

    val _trips = ArrayList<Trip>()
    var shape = ArrayList<CoordinatePoint>()
    var stops = ArrayList<Stop>()

    fun getRouteData(routeID: String): Single<List<Trip>> {
        return Single.create<List<Trip>?> { emitter ->
            val listener = Firebase.database.reference
                .child("routes")
                .child(routeID)
                .addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        _trips.clear()
                        for (trip in snapshot.children) {
                            /*
                            shape.clear()
                            for (shapeCoords in trip.child("shape").children) {
                                shapeCoords.getValue<CoordinatePoint>()?.let { shape.add(it) }
                            }
                            stops.clear()
                            for (stop in trip.child("stopSequence").children) {
                                stop.getValue<Stop>()?.let { stops.add(it) }
                            }
                             */

                            _trips.add(
                                Trip(
                                    trip.key,
                                    trip.child("origin").getValue<String>(),
                                    trip.child("destination").getValue<String>(),
                                    null,
                                    null)
                            )
                        }
                        Log.i(TAG, "emitter on success. _trips size: " + _trips.size.toString())
                        if (_trips.size == 0) {
                            emitter.onError(Throwable("No results found. Please check bus line"))
                        } else {
                            emitter.onSuccess(_trips)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(TAG, "Error: " + error.message)
                        Log.i(TAG, "emitter on error")
                        emitter.onError(null)
                    }
                })
        }
    }
}