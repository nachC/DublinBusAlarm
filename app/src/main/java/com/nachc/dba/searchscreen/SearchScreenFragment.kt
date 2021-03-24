package com.nachc.dba.searchscreen

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.*
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.gson.Gson
import com.nachc.dba.sharedviewmodel.MainViewModel
import com.nachc.dba.R
import com.nachc.dba.databinding.SearchScreenFragmentBinding
import com.nachc.dba.models.Favourite
import com.nachc.dba.models.Trip
import com.nachc.dba.ui.MainScreenFragmentDirections
import com.nachc.dba.util.SharedPreferencesHelper
import com.nachc.dba.util.isInternetAvailable
import java.util.*

class SearchScreenFragment : Fragment() {

    private val TAG = "SearchScreenFragment"
    private val LOCATION_SETTINGS_CODE = 0
    private val BATTERY_OPT_SETTINGS_CODE = 1
    private val FINE_LOCATION = 1 // request code for fine location access permission

    private lateinit var sharedPref: SharedPreferencesHelper

    private var isConnected: Boolean = false // flag for internet connectivity
    private var loading: Boolean = false

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var binding: SearchScreenFragmentBinding

    // Observer to be aware of loading process
    private val loadingObserver = Observer<Boolean> { isLoading ->
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        loading = isLoading
        if (isLoading) {
            binding.loadError.visibility = View.GONE
        }
    }

    // Observer to be aware of any error from the backend
    private val errorObserver = Observer<Pair<Boolean, String>> { isError ->
        if (isError.first) {
            binding.loadError.text = isError.second
            binding.loadError.visibility = View.VISIBLE
        } else {
            View.GONE
        }
    }

    // Observer to be aware of input validation results
    // as this is the first thing to check, we'll hide the keyboard here
    private val validInputObserver = Observer<Pair<Boolean, String>> { isValidInput ->
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.inputLineEditText.windowToken, 0)
        if (!isValidInput.first) {
            // input is not valid, show respective error
            binding.inputLineEditText.error = isValidInput.second
        }
    }

    // Observer to be aware of the trips being set
    private val tripsObserver = Observer<List<Trip>> { trips ->
        // trips will be set to null on onViewCreated because we could be coming back from routelist fragment
        if (trips != null) {
            viewModel.routeName.value = binding.inputLineEditText.text.toString()
            findNavController().navigate(MainScreenFragmentDirections.actionMainScreenToRouteList())
        }
    }
    // Observer for favourites

    private val favouritesObserver = Observer<List<Favourite>> { favourites ->
        binding.chipGroup.removeAllViews()
        for (fav in favourites) {
            val chip = Chip(context).apply {
                isCloseIconVisible = true
                text = fav.id
                setOnClickListener {
                    val tripString = fav.trip
                    val favTrip = Gson().fromJson(tripString, Trip::class.java)
                    findNavController().navigate(
                        MainScreenFragmentDirections.favouriteToMaps(favTrip)
                    )
                }
                setOnCloseIconClickListener {
                    viewModel.deleteFavouriteById(fav.id)
                    binding.chipGroup.removeView(it)
                    Toast.makeText(requireContext(), "Favourite deleted", Toast.LENGTH_SHORT).show()
                }
            }
            binding.chipGroup.addView(chip)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.search_screen_fragment, container, false
        )

        sharedPref = SharedPreferencesHelper(requireContext())

        // Set the ViewModel for databinding - this allows the bound layout access to all of the
        // data in the VieWModel
        binding.mainViewModel = viewModel

        // Specify the current activity as the lifecycle owner of the binding. This is used so that
        // the binding can observe LiveData updates
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (!sharedPref.hasShownIntro()) {
            sharedPref.setShownIntro(true)
            // check if we have location permissions
            if (checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                binding.searchBtn.isEnabled = true
            } else {
                // request location permissions
                binding.searchBtn.isEnabled = false
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), FINE_LOCATION)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * Here we check for location permission only IF IT ISN'T a fresh install
         * because the user could've revoked permission between sessions.
         * IF IT IS a fresh install, we don't check it here because at this point the user
         * is on the app intro screens where we request location permissions.
         * ***user sharedpreferences*** key=SHOWN_INTRO
         * */

        if (sharedPref.hasShownIntro()) {
            // check if we have location permissions
            if (checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                binding.searchBtn.isEnabled = true
            } else {
                // request location permissions
                binding.searchBtn.isEnabled = false
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), FINE_LOCATION)
            }
        }

        // Check for network connectivity. We'll allow the search functionality only if a connection is available
        isInternetAvailable(requireContext()) { result -> isConnected = result }

        // set trips to null in case we come from the routeList fragment by pressing the back button
        viewModel.resetTrips()
        viewModel.getAllFavs()

        binding.permissionBtn.setOnClickListener {
            openPermissionSettings(LOCATION_SETTINGS_CODE)
        }
        binding.allowLocationBtn.setOnClickListener {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), FINE_LOCATION)
        }
        /*
        binding.dataSavedTextView.setOnClickListener {
            showDataSavedDialog()
        }
         */
        binding.searchBtn.setOnClickListener {
            if (!isConnected) {
                Toast.makeText(
                    context,
                    "Please, check your internet connection",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (!loading) {
                viewModel.search(binding.inputLineEditText.text.toString().toLowerCase(Locale.ROOT))
            }
        }

        // start observing the ViewModel
        viewModel.validInput.observe(viewLifecycleOwner, validInputObserver)
        viewModel.loading.observe(viewLifecycleOwner, loadingObserver)
        viewModel.loadError.observe(viewLifecycleOwner, errorObserver)
        viewModel.trips.observe(viewLifecycleOwner, tripsObserver)
        viewModel.favourites.observe(viewLifecycleOwner, favouritesObserver)
    }

    // here we handle the result of calling requestPermissions
    // if user provided permission for FINE LOCATION we proceed to call search on the viewModel
    // if user denies, we show Toast with info
    // if user denies and checks "don't ask again", we show a settings button to redirect user to permissions settings
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == FINE_LOCATION) {
            // received request permission for fine-location
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // we have permission to use location -> we can search now
                binding.searchBtn.isEnabled = true
                binding.allowLocationBtn.visibility = View.GONE
            } else {
                // we don't have location permission
                Toast.makeText(context, "Permission was not granted", Toast.LENGTH_SHORT).show()
                // see if user checked "Don't ask again" box before, if so, show button to open OS Settings
                if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Log.i(TAG, "redirect user to settings")
                    binding.allowLocationBtn.visibility = View.GONE
                    binding.permissionBtn.visibility = View.VISIBLE
                } else {
                    binding.permissionBtn.visibility = View.GONE
                    binding.allowLocationBtn.visibility = View.VISIBLE
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    // method called when clicking the permission button
    // we send the user to the OS settings screen to set location permission manually
    fun openPermissionSettings(requestCode: Int) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", activity?.packageName, null)
        intent.data = uri
        // we'll handle the result on onActivityResult
        startActivityForResult(intent, requestCode)
    }

    // if we come from the SETTINGS screen and user provided location permission
    // then we can search
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i(TAG, "onActivityResult")
        if (requestCode == LOCATION_SETTINGS_CODE) {
            Log.i(TAG, "onActivityResult in fragment request code 0")
            if (checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                binding.permissionBtn.visibility = View.GONE
                binding.searchBtn.isEnabled = true
                Toast.makeText(this.context, "You can search now", Toast.LENGTH_SHORT).show()
            } else {
                // request location permissions
                binding.searchBtn.isEnabled = false
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), FINE_LOCATION)
            }
        }
    }

    // method to show an AlertDialog with information
    // regarding what data is saved to the DB
    /*
    fun showDataSavedDialog() {
        AlertDialog.Builder(activity)
            .setTitle("Data saved")
            .setMessage(
                """ 
            Four things are saved to the database: 
            - The coordinates of the stop you selected. 
            - Your coordinates at the moment you select a stop. 
            - The time it took for the bus to reach the selected stop. 
            - The date the trip was made.
            No data is saved that could identify you or your device.
            The goal is to gain insights in what routes and stops are frequently used, and the time it takes for the given trips. 
            """.trimIndent()
            ) // A null listener allows the button to dismiss the dialog and take no further action.
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }
     */
}