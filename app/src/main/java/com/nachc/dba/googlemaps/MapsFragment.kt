package com.nachc.dba.googlemaps

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Notification
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.provider.AlarmClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
import com.nachc.dba.R
import com.nachc.dba.models.Session
import com.nachc.dba.models.Trip
import com.nachc.dba.receivers.AlarmReceiver
import com.nachc.dba.util.drawPolylines
import com.nachc.dba.util.drawStopMarkers
import com.nachc.dba.util.startAlarm
import com.nachc.dba.util.startLocationService
import java.util.*

@SuppressLint("MissingPermission")
class MapsFragment : Fragment() {

    private val TAG = "MapsFragment"
    private val CAMERA_ZOOM = 13F
    private val REQUEST_CHECK_SETTINGS = 526
    private val TRIGGER_DISTANCE_TO_STOP = 50F
    private val ALARM_DELAY = 500
    private val NOTIFICATION_DISMISS = "dismiss"

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private val viewModel: MapsViewModel by viewModels()

    // session variables to write to DB
    private var sessionUserOriginCoords = ArrayList<Double>()
    private var sessionSelectedStopCoords = ArrayList<Double>()
    private var sessionDate : Long = 0 // (epoch time) moment when the user reaches the selected stop
    private var sessionTimeTakenToStop : Long = 0 // time in seconds between stop selected and stop reached

    private var startTripDate : Long = 0 // (epoch time) holds the exact moment in time when the user selects a stop (used to calculate sessionTimeTakenToStop)

    lateinit var googleMap: GoogleMap
    private var selectedStop: Location = Location("")
    private var currentMarker: Marker? = null
    private var alertDialog: AlertDialog? = null

    private var isStopSelected: Boolean = false
    private var isStopReached: Boolean = false

    lateinit var trip: Trip

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        this.googleMap = googleMap

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PERMISSION_GRANTED) {
            googleMap.isMyLocationEnabled = true
            createLocationRequest()
        } else {
            // user shouldn't be on the Maps view without previously providing location permission.
            // in the case where permissions are removed while on the Maps view or Routelist view
            // we'll redirect the user to the main screen,
            // as the search functionality shouldn't work without permissions granted
            if (alertDialog != null) {
                alertDialog!!.dismiss()
            }
            findNavController().navigate(MapsFragmentDirections.actionMapsToSearchScreen())
        }

        val firstStop = LatLng(
            trip.stopSequence!![0].lat!!.toDouble(),
            trip.stopSequence!![0].lng!!.toDouble()
        )
        // set marker for every stop in the given trip
        drawStopMarkers(googleMap, trip.stopSequence!!)
        // set polyline to draw the trajectory of the bus for the given trip
        drawPolylines(googleMap, trip.shape!!)

        // move camera to the first stop
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstStop, CAMERA_ZOOM))

        googleMap.setOnMarkerClickListener { marker ->
            currentMarker.let {
                it?.setIcon(BitmapDescriptorFactory.defaultMarker())
            }
            currentMarker = marker

            selectedStop.latitude = currentMarker.let {
                it!!.position.latitude
            }
            selectedStop.longitude = currentMarker.let {
                it!!.position.longitude
            }
            //change color of selected marker
            currentMarker.let {
                it?.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            }

            // clear arraylist in case the user selected different stop before
            // there should only be two elements on these arraylists (lat, lng)
            sessionUserOriginCoords.clear()
            sessionSelectedStopCoords.clear()
            // set sessionSelectedStopCoords
            sessionSelectedStopCoords.add(currentMarker!!.position.latitude)
            sessionSelectedStopCoords.add(currentMarker!!.position.longitude)
            isStopSelected = true

            fusedLocationClient.lastLocation?.addOnSuccessListener {
                // here we'll save set Session's userCoords data
                Log.i(TAG, it.latitude.toString() + " " + it.longitude.toString())
            }

            fusedLocationClient.lastLocation.addOnSuccessListener {
                it.let {
                    sessionUserOriginCoords.add(it.latitude)
                    sessionUserOriginCoords.add(it.longitude)
                }
            }
            startTripDate = Date().time
            startLocationService(requireContext())
            false
        }
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
        // assign it to local variable and update the recyclerview list
        arguments.let {
            trip = MapsFragmentArgs.fromBundle(it!!).trip
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        /**
         * callback object to be used on createLocationRequest()
         * */
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    if (isStopSelected) {
                        if (location.distanceTo(selectedStop) < TRIGGER_DISTANCE_TO_STOP && !isStopReached) {
                            Log.i(TAG, "close to stop -> trigger alarm")
                            isStopReached = true
                            isStopSelected = false

                            sessionDate = Date().time
                            sessionTimeTakenToStop = (sessionDate - startTripDate)/1000

                            viewModel.saveSessionData(Session(
                                sessionUserOriginCoords,
                                sessionSelectedStopCoords,
                                sessionTimeTakenToStop,
                                sessionDate
                            ))

                            startAlarm(requireContext(), ALARM_DELAY)

                            //alert dialog to allow the user to stop the alarm from the maps activity
                            alertDialog = AlertDialog.Builder(requireContext())
                                .setTitle("Time to get out!")
                                .setMessage("You've arrived to your destination") // A null listener allows the button to dismiss the dialog and take no further action.
                                .setPositiveButton("stop") { _: DialogInterface?, _: Int ->
                                    val dismissIntent = Intent(
                                        requireContext(),
                                        AlarmReceiver::class.java
                                    )
                                    dismissIntent.action = AlarmClock.ACTION_DISMISS_ALARM
                                    dismissIntent.putExtra(
                                        Notification.EXTRA_NOTIFICATION_ID,
                                        NOTIFICATION_DISMISS
                                    )
                                    requireContext().sendBroadcast(dismissIntent)
                                }
                                .show()
                        }
                    }
                }
            }
        }

        // inform the user how to select a Stop
        alertDialog = AlertDialog.Builder(activity)
            .setTitle("Select your Stop")
            .setMessage(R.string.alertDialog_info)
            // A null listener allows the button to dismiss the dialog and take no further action.
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

    private fun createLocationRequest() {
        val locationRequest = LocationRequest.create()?.apply {
            interval = 3000
            fastestInterval = 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest!!)

        val client: SettingsClient = LocationServices.getSettingsClient(requireActivity())
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { _ ->
            // All location settings are satisfied. The client can initialize
            // location requests here.
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(
                        requireActivity(),
                        REQUEST_CHECK_SETTINGS
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.e(TAG, sendEx.message)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // if the alertDialog for stopping the alarm is being shown then dismiss it before exiting activity
        if (alertDialog != null) {
            alertDialog!!.dismiss()
        }
    }
}