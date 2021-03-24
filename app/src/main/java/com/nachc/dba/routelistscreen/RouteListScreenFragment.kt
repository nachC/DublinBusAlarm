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
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.gson.Gson
import com.nachc.dba.MainViewModel
import com.nachc.dba.R
import com.nachc.dba.databinding.RouteListScreenFragmentBinding
import com.nachc.dba.models.Trip

class RouteListScreenFragment : Fragment() {

    val TAG: String = "RouteListScreenFragment"

    private val REQUEST_CHECK_SETTINGS = 214
    private val REQUEST_ENABLE_GPS = 516

    private lateinit var trips: List<Trip>
    private lateinit var routeName: String

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var binding: RouteListScreenFragmentBinding

    private val listAdapter = RouteListAdapter(arrayListOf()) { position, tag ->
        if (tag == "save") {
            viewModel.saveFavourite(
                "$routeName " + trips[position].direction,
                trips[position].direction.toString(),
                Gson().toJson(trips[position])
            )
            Toast.makeText(requireContext(), "Favourite Saved!", Toast.LENGTH_SHORT).show()
        }/* else if (tag == "delete") {
            viewModel.deleteFavourite(
                Favourite("$routeName " + trips[position].direction,
                    trips[position].direction.toString(),
                    Gson().toJson(trips[position])))
        }*/
    }
    private val routeNameObserver = Observer<String> {
        it?.let {
            routeName = it
        }
    }
    private val tripsObserver = Observer<List<Trip>> {
        it?.let {
            trips = it
            listAdapter.updateTripList(trips)
        }
    }

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val locationSettingsRequest: LocationSettingsRequest

        // check if user has location enabled
        // if not, show dialog to turn on location or send to settings
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY))
        builder.setAlwaysShow(true)
        locationSettingsRequest = builder.build()

        LocationServices.getSettingsClient(requireActivity())
            .checkLocationSettings(locationSettingsRequest)
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

        viewModel.trips.observe(viewLifecycleOwner, tripsObserver)
        viewModel.routeName.observe(viewLifecycleOwner, routeNameObserver)

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