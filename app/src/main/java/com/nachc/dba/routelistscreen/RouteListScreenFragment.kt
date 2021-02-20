package com.nachc.dba.routelistscreen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.nachc.dba.R
import com.nachc.dba.databinding.RouteListScreenFragmentBinding
import com.nachc.dba.models.Trip

class RouteListScreenFragment : Fragment() {

    val TAG: String = "RouteListScreenFragment"

    private val REQUEST_CHECK_SETTINGS = 214
    private val REQUEST_ENABLE_GPS = 516

    private val listAdapter = RouteListAdapter(arrayListOf())

    var trips: List<Trip>? = null

    private lateinit var binding: RouteListScreenFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.route_list_screen_fragment,
            container,
            false
        )
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val locationSettingsRequest: LocationSettingsRequest

        // check if user has location enabled
        // if not, show dialog to turn on location or send to settings
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY))
        builder.setAlwaysShow(true)
        locationSettingsRequest = builder.build()

        LocationServices.getSettingsClient(requireActivity())
            .checkLocationSettings(locationSettingsRequest)
            .addOnSuccessListener { }
            .addOnFailureListener { e: Exception ->
                when ((e as ApiException).statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val rae = e as ResolvableApiException
                        rae.startResolutionForResult(
                            requireActivity(),
                            REQUEST_CHECK_SETTINGS
                        )
                    } catch (sie: SendIntentException) {
                        Log.e("GPS", "Unable to execute request.")
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> Log.e(
                        "GPS",
                        "Location settings are inadequate, and cannot be fixed here. Fix in Settings."
                    )
                }
            }
            .addOnCanceledListener {
                Log.e(
                    "GPS",
                    "checkLocationSettings -> onCanceled"
                )
            }

        // retrieve argument passed to fragment by navigation
        // assign it to local variable and update the recyclerview list
        arguments.let {
            trips = RouteListScreenFragmentArgs.fromBundle(it!!).trips.toList()
            listAdapter.updateTripList(trips!!)
        }

        binding.routeList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = listAdapter
        }
    }

    // handles the action of opening the Settings view to turn on Gps
    private fun openGpsEnableSetting() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivityForResult(
            intent,
            REQUEST_ENABLE_GPS
        )
    }

    // handles the result of opening Settings
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                }
                Activity.RESULT_CANCELED -> {
                    Log.e("GPS", "User denied to access location")
                    openGpsEnableSetting()
                }
            }
        } else if (requestCode == REQUEST_ENABLE_GPS) {
            val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            if (!isGpsEnabled) {
                openGpsEnableSetting()
            }
        }
    }
}