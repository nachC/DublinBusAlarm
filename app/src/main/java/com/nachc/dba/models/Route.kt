package com.nachc.dba.models

/**
 * A Route is identified by a route name or id (e.g. 66b)
 * Each route will do different Trips.
 * Each Trip has an Origin and a Destination,
 * a sequence of Stops that the bus follows,
 * a sequence of coordinates called a Shape, used to draw a 'pretty' trajectory on the map.
 * Each Stop has a name and a set of coordinates (lat, lng).
 */

data class Route(
    val trips: MutableList<Trip>?
)

data class Trip(
    val origin: String?,
    val destination: String?,
    val shape: ArrayList<ArrayList<String>>?,
    val stops: ArrayList<Stop>?
)

data class Stop(
    val name: String?,
    val latlng: ArrayList<String>?,
    val lat: Double?,
    val lng: Double?
)