package com.nachc.dba.util

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.nachc.dba.models.CoordinatePoint
import com.nachc.dba.models.Stop

fun drawPolylines(googleMap: GoogleMap, shape: List<CoordinatePoint>) {
    for (index in 0 until (shape.size - 1)) {
        googleMap.addPolyline(
            PolylineOptions()
                .add(
                    LatLng(
                        shape[index].lat!!.toDouble(),
                        shape[index].lng!!.toDouble()
                    )
                )
                .add(
                    LatLng(
                        shape[index + 1].lat!!.toDouble(),
                        shape[index + 1].lng!!.toDouble()
                    )
                )
        )
    }
}

fun drawStopMarkers(googleMap: GoogleMap, stopSequence: List<Stop>) {
    for (stop in stopSequence) {
        googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(stop.lat!!.toDouble(), stop.lng!!.toDouble()))
                .title(stop.name)
        )
    }
}