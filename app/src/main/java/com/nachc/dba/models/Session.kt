package com.nachc.dba.models

/**
 * The Session class describes the information we want to save on the database,
 * regarding the user's use of the app.
 * In particular, we'll store:
 * - user's location coordinates (lat, lng) when a stop is selected on the map
 * - selected stop's coordinates (lat, lng)
 * - time it took for the bus to reach the selected stop (since the stop was selected until it was reached)
 * - the date the trip was made
 */

data class Session(
    val userOriginCoords: ArrayList<Double>,
    val selectedStopCoords: ArrayList<Double>,
    val timeTakenToStop: Long,
    val date: Long
)