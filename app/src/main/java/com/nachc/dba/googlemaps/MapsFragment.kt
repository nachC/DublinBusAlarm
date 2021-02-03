package com.nachc.dba.googlemaps

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.nachc.dba.R
import com.nachc.dba.models.Trip

class MapsFragment : Fragment() {

    private val TAG = "MapsFragment"
    private val CAMERA_ZOOM = 13F
    //private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var trip: Trip

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        val firstStop = LatLng(trip.stopSequence!![0].lat!!.toDouble(), trip.stopSequence!![0].lng!!.toDouble())
        // set marker for every stop in the given trip
        for (stop in trip.stopSequence!!) {
            googleMap.addMarker(MarkerOptions()
                .position(LatLng(stop.lat!!.toDouble(), stop.lng!!.toDouble()))
                .title(stop.name))
        }
        // set polyline to draw the trajectory of the bus for the given trip
        for (index in 0 until (trip.shape!!.size-1)) {
            googleMap.addPolyline(PolylineOptions()
                .add(LatLng(trip.shape!![index].lat!!.toDouble(), trip.shape!![index].lng!!.toDouble()))
                .add(LatLng(trip.shape!![index+1].lat!!.toDouble(), trip.shape!![index+1].lng!!.toDouble())))
        }

        googleMap.setOnMarkerClickListener {
            Log.i(TAG, it.position.latitude.toString())
            false
        }

        // move camera to the first stop
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstStop, CAMERA_ZOOM))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        // retrieve argument passed to fragment by navigation
        // asign it to local variable and update the recyclerview list
        arguments?.let {
            trip = MapsFragmentArgs.fromBundle(it).trip
        }

        //fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // inform the user how to select a Stop
        AlertDialog.Builder(activity)
            .setTitle("Select your Stop")
            .setMessage("Tap the Stop where you want the alarm to ring. Once you're close to it, I will let you know!")
            // A null listener allows the button to dismiss the dialog and take no further action.
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }
}