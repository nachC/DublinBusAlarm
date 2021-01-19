package com.nachc.dba.searchscreen

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.nachc.dba.R
import com.nachc.dba.databinding.SearchScreenFragmentBinding

class SearchScreenFragment : Fragment() {

    /**
     * TODO:
     * */

    companion object {
        fun newInstance() = SearchScreenFragment()
    }

    // tag for logging
    val TAG: String = "SearchScreenFragment"
    // request code for fine location access permission
    val FINE_LOCATION = 1
    // viewModel reference
    private val viewModel: SearchScreenViewModel by viewModels()
    // binding reference
    private lateinit var binding: SearchScreenFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.search_screen_fragment, container, false
        )

        // Specify the current activity as the lifecycle owner of the binding. This is used so that
        // the binding can observe LiveData updates
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        /*****Click events*****/
        binding.searchBtn.setOnClickListener {
            Log.i(TAG, "button clicked")
            // Hide the keyboard after clicking search
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.inputLineEditText.windowToken, 0)
            // call the viewModel for validation on the user's input
            viewModel.validateInput(binding.inputLineEditText.text.toString())
        }
        binding.permissionBtn.setOnClickListener {
            openPermissionSettings()
        }
        binding.dataSavedTextView.setOnClickListener {
            showDataSavedDialog()
        }
        /*****End Click events*****/

        viewModel.isValidInput.observe(viewLifecycleOwner, { isValidInput ->
            if (!isValidInput.first) {
                binding.inputLineEditText.error = isValidInput.second
            } else {
                searchRoute()
            }
        })

        // observe news broadcast, will let us know if the route search was successful or not
        viewModel.news.observe(viewLifecycleOwner, { goodNews ->
            Log.i(TAG, "hey! we got an update from observable")
            if (goodNews) {
                Log.i(TAG, "good news")
                Toast.makeText(this.context, "good news!", Toast.LENGTH_SHORT).show()
                //findNavController().navigate(SearchScreenFragmentDirections.actionSearchScreenToRouteListScreen())
            } else {
                Log.i(TAG, "bad news")
                Toast.makeText(this.context, "bad news!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // request permissions. Will get called when search btn is pressed
    private fun searchRoute() {
        // request permission to use location -> result will be handled by onRequestPermissionsResult
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), FINE_LOCATION)
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
                viewModel.search(binding.inputLineEditText.text.toString())
            } else {
                // we don't have location permission
                Toast.makeText(this.context, "Permission was not granted", Toast.LENGTH_SHORT).show();
                // see if user checked "Don't ask again" box before, if so, show button to open OS Settings
                if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Log.i(TAG, "redirect user to settings")
                    binding.permissionBtn.visibility = View.VISIBLE
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    // method called when clicking the permission button
    // we send the user to the OS settings screen to set location permission manually
    fun openPermissionSettings() {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", activity?.packageName, null)
            intent.data = uri
            // here we route the user to the Settings menu to set the permissions manually
            // we'll handle the result with the onActivityResult method
            startActivityForResult(intent, 0)
    }

    // if we come from the SETTINGS screen and user provided location permission
    // then we can search
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            binding.permissionBtn.visibility = View.INVISIBLE
            Toast.makeText(this.context, "You can search now", Toast.LENGTH_SHORT).show();
        }
    }

    // method to show a AlertDialog with information
    // regarding what data is saved to the DB
    fun showDataSavedDialog() {
        AlertDialog.Builder(activity)
            .setTitle("Data we save")
            .setMessage(
                """
            When the alarm triggers, we save some data to our database. 
            We save exactly four things: 
            - The coordinates of the stop you selected. 
            - Your coordinates at the moment you select a stop 
            - The time it took for the bus to reach the selected stop 
            - The date the trip was made
            """.trimIndent()
            ) // A null listener allows the button to dismiss the dialog and take no further action.
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }
}