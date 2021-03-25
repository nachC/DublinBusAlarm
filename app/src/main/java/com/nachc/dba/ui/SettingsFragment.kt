package com.nachc.dba.ui

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.slider.Slider
import com.nachc.dba.R
import com.nachc.dba.databinding.FragmentSettingsBinding
import com.nachc.dba.util.SharedPreferencesHelper
import com.nachc.dba.util.startAlarm

class SettingsFragment : Fragment() {

    private val TAG = "SettingsFragment"

    private var alarmOnGoing: Boolean = false // flag for testing

    private lateinit var sharedPref: SharedPreferencesHelper
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)

        sharedPref = SharedPreferencesHelper(requireContext())

        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.distanceSlider.value = sharedPref.getTriggerDistToStopFromSettings()

        binding.testAlarmBtn.setOnClickListener {
            if (!alarmOnGoing) {
                alarmOnGoing = true
                startAlarm(requireContext(), 500)
            }
        }
        binding.testAlarmHelp.setOnClickListener {
            AlertDialog.Builder(requireContext()).apply {
                setTitle(R.string.testAlarmHelpDialogTitle)
                setMessage(R.string.testAlarmHelpDialogText)
                setPositiveButton(R.string.settings) { _, _ -> openPermissionSettings() }
                setNegativeButton(android.R.string.cancel, null)
            }.show()
        }
        binding.distanceSlider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStopTrackingTouch(slider: Slider) {
                // save distance value to sharedpreferences
                sharedPref.setTriggerDistToStop(slider.value)
            }

            override fun onStartTrackingTouch(slider: Slider) {
            }
        })
    }

    fun openPermissionSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", activity?.packageName, null)
        intent.data = uri
        startActivity(intent)
    }
}