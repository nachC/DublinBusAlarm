package com.nachc.dba.ui

import android.app.AlertDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.preference.PreferenceManager
import com.google.android.material.slider.Slider
import com.nachc.dba.R
import com.nachc.dba.databinding.FragmentSettingsBinding
import com.nachc.dba.util.openPermissionSettings
import com.nachc.dba.util.startAlarm

class SettingsFragment : Fragment() {

    private val TAG = "SettingsFragment"

    private var alarmOnGoing: Boolean = false // flag for testing
    private val MAP_DISTANCE_KEY = "MAP_DISTANCE"

    private lateinit var sharedPref: SharedPreferences
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
        binding.lifecycleOwner = this

        sharedPref = PreferenceManager.getDefaultSharedPreferences(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sliderValue = sharedPref.getFloat(MAP_DISTANCE_KEY, 100f)
        binding.distanceSlider.value = sliderValue

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
                setPositiveButton(R.string.settings) { _, _ -> openPermissionSettings(requireActivity(), 101) }
                setNegativeButton(android.R.string.cancel, null)
            }.show()
        }
        binding.distanceSlider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStopTrackingTouch(slider: Slider) {
                // save distance value to sharedpreferences
                sharedPref.edit().putFloat(MAP_DISTANCE_KEY, slider.value).apply()
            }

            override fun onStartTrackingTouch(slider: Slider) {
            }
        })
    }
}