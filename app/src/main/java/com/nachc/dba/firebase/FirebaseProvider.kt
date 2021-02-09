package com.nachc.dba.firebase

import android.util.Log
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.nachc.dba.models.CoordinatePoint
import com.nachc.dba.models.Session
import com.nachc.dba.models.Stop
import com.nachc.dba.models.Trip
import io.reactivex.rxjava3.core.Single

class FirebaseProvider {

    private val TAG = "FirebaseProvider"

    val _trips = ArrayList<Trip>()

    fun getRouteData(routeID: String): Single<List<Trip>> {
        return Single.create { emitter ->
            Firebase.database.reference
                .child("routes")
                .child(routeID)
                .addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        _trips.clear()
                        for (trip in snapshot.children) {
                            _trips.add(
                                Trip(
                                    trip.key,
                                    trip.child("origin").getValue<String>(),
                                    trip.child("destination").getValue<String>(),
                                    trip.child("direction").getValue<String>(),
                                    trip.child("shape").getValue<List<CoordinatePoint>>(),
                                    trip.child("stop_sequence").getValue<List<Stop>>())
                            )
                        }
                        if (_trips.size == 0) {
                            emitter.onError(Throwable("No results found. Please check bus line"))
                        } else {
                            Log.i(TAG, "emitter on success. _trips size: " + _trips.size.toString())
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

    fun saveSessionData(session: Session): Single<String> {
        return Single.create { emitter ->
            Firebase.database.reference
                .child("sessions")
                .push()
                .setValue(session)
                .addOnSuccessListener {
                    emitter.onSuccess("Successful save")
                }
                .addOnFailureListener {
                    emitter.onError(it)
                }
        }
    }
}