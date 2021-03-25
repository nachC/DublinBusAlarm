package com.nachc.dba.util

import android.content.Context
import androidx.preference.PreferenceManager

class SharedPreferencesHelper(context: Context) {

    private val MAP_DISTANCE_KEY = "MAP_DISTANCE"
    private val APP_INTRO_KEY = "SHOWN_INTRO"

    private val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)

    fun hasShownIntro() = sharedPref.getBoolean(APP_INTRO_KEY, false)

    fun setShownIntro(value: Boolean) = sharedPref.edit().putBoolean(APP_INTRO_KEY, value).apply()

    fun getTriggerDistToStopFromSettings() = sharedPref.getFloat(MAP_DISTANCE_KEY, 100f)

    fun setTriggerDistToStop(dist: Float) = sharedPref.edit().putFloat(MAP_DISTANCE_KEY, dist).apply()
}