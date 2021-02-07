package com.nachc.dba.services

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.IBinder
import android.util.Log

class RingtoneService: Service() {

    private val TAG = "RingtoneService"

    private var ringtone: Ringtone? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "onStartCommand called")
        ringtone = RingtoneManager.getRingtone(applicationContext, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
        ringtone!!.audioAttributes = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build()
        ringtone!!.play()

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "Stopping ringtone")
        ringtone!!.stop()
    }
}