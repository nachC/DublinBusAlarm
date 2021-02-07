package com.nachc.dba.googlemaps

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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
import com.nachc.dba.models.Trip
import com.nachc.dba.receivers.AlarmReceiver
import com.nachc.dba.services.LocationService


@SuppressLint("MissingPermission")
class MapsFragment : Fragment() {

    /**
     * TODO:
     *  - implement session repo to save session data
     *  - locationservice should stop when navigating back from mapsfragment
     * */

    private val TAG = "MapsFragment"
    private val CAMERA_ZOOM = 13F
    private val REQUEST_CHECK_SETTINGS = 526
    private val TRIGGER_DISTANCE_TO_STOP = 50F
    private val ALARM_DELAY = 500

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

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

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) {
            googleMap.isMyLocationEnabled = true
            createLocationRequest()
        } else {
            // user shouldn't be on the Maps view without before providing location permission.
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
        for (stop in trip.stopSequence!!) {
            googleMap.addMarker(
                MarkerOptions()
                    .position(LatLng(stop.lat!!.toDouble(), stop.lng!!.toDouble()))
                    .title(stop.name)
            )
        }
        // set polyline to draw the trajectory of the bus for the given trip
        for (index in 0 until (trip.shape!!.size - 1)) {
            googleMap.addPolyline(
                PolylineOptions()
                    .add(
                        LatLng(
                            trip.shape!![index].lat!!.toDouble(),
                            trip.shape!![index].lng!!.toDouble()
                        )
                    )
                    .add(
                        LatLng(
                            trip.shape!![index + 1].lat!!.toDouble(),
                            trip.shape!![index + 1].lng!!.toDouble()
                        )
                    )
            )
        }

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
            isStopSelected = true
            //change color of selected marker
            currentMarker.let {
                it?.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            }

            fusedLocationClient.lastLocation?.addOnSuccessListener {
                // here we'll save set Session's userCoords data
                Log.i(TAG, it.latitude.toString() + " " + it.longitude.toString())
            }

            startLocationService()

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
        // asign it to local variable and update the recyclerview list
        arguments?.let {
            trip = MapsFragmentArgs.fromBundle(it).trip
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        /**
         * callback object to be user on createLocationRequest()
         * */
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    if (isStopSelected) {
                        if (location.distanceTo(selectedStop) < TRIGGER_DISTANCE_TO_STOP && !isStopReached) {
                            Log.i(TAG, "close to stop")
                            isStopReached = true
                            isStopSelected = false
                            startAlarm()
                        }
                    }
                }
            }
        }

        // inform the user how to select a Stop
        alertDialog = AlertDialog.Builder(activity)
            .setTitle("Select your Stop")
            .setMessage("Tap the stop where you want the alarm to ring. Once you're close to it, I will let you know!")
            // A null listener allows the button to dismiss the dialog and take no further action.
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

    fun createLocationRequest() {
        val locationRequest = LocationRequest.create()?.apply {
            interval = 3000
            fastestInterval = 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest!!)

        val client: SettingsClient = LocationServices.getSettingsClient(requireActivity())
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { locationSettingsResponse ->
            // All location settings are satisfied. The client can initialize
            // location requests here.
            // ...
            Log.i(TAG, locationSettingsResponse.locationSettingsStates.isGpsUsable.toString())
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
                    // Ignore the error.
                }
            }
        }
    }

    fun startLocationService() {
        Log.d(TAG, "startLocationService called.")
        Intent(requireContext(), LocationService::class.java).also { intent ->
            requireContext().startService(intent)
        }
    }

    fun startAlarm() {
        Log.i(TAG, "startAlarm called")
        val alarmIntent = Intent(requireContext(), AlarmReceiver::class.java)
        val alarmManager = context?.getSystemService(ALARM_SERVICE) as AlarmManager
        val pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, alarmIntent, 0)
        val alarmClockInfo: AlarmManager.AlarmClockInfo = AlarmManager.AlarmClockInfo(
            System.currentTimeMillis() + ALARM_DELAY,
            pendingIntent
        )
        alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        // if the alertDialog for stopping the alarm is being shown then dismiss it before exiting activity
        if (alertDialog != null) {
            alertDialog!!.dismiss()
        }
    }

}