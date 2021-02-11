package com.nachc.dba.models

import android.os.Parcel
import android.os.Parcelable

/**
 * A Route is identified by a route name or id (e.g. 66b)
 * Each route will do different Trips.
 * Each Trip has an Origin and a Destination,
 * a sequence of Stops that the bus follows,
 * a sequence of coordinates called a Shape, used to draw a 'pretty' trajectory on the map.
 * Each Stop has a name and a set of coordinates (lat, lng).
 */

@Suppress("UNCHECKED_CAST")
data class Trip(
    val id: String?,
    val origin: String?,
    val destination: String?,
    val direction: String?,
    val shape: List<CoordinatePoint>?,
    val stopSequence: List<Stop>?
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createTypedArrayList(CoordinatePoint),
        parcel.createTypedArrayList(Stop)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(origin)
        parcel.writeString(destination)
        parcel.writeString(direction)
        parcel.writeTypedList(shape)
        parcel.writeTypedList(stopSequence)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Trip> {
        override fun createFromParcel(parcel: Parcel): Trip {
            return Trip(parcel)
        }

        override fun newArray(size: Int): Array<Trip?> {
            return arrayOfNulls(size)
        }
    }
}

data class Stop(
    val name: String? = "",
    //val latlng: CoordinatePoint?,
    val lat: String? = "",
    val lng: String? = ""
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(lat)
        parcel.writeString(lng)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Stop> {
        override fun createFromParcel(parcel: Parcel): Stop {
            return Stop(parcel)
        }

        override fun newArray(size: Int): Array<Stop?> {
            return arrayOfNulls(size)
        }
    }
}

data class CoordinatePoint(
    val lat: String? = "",
    val lng: String? = ""
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(lat)
        parcel.writeString(lng)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CoordinatePoint> {
        override fun createFromParcel(parcel: Parcel): CoordinatePoint {
            return CoordinatePoint(parcel)
        }

        override fun newArray(size: Int): Array<CoordinatePoint?> {
            return arrayOfNulls(size)
        }
    }
}