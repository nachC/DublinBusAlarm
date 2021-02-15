package com.nachc.dba.ui

import android.app.Notification
import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.nachc.dba.R
import com.nachc.dba.receivers.AlarmReceiver

class AlarmLockScreenActivity : AppCompatActivity() {

    private val NOTIFICATION_DISMISS = "dismiss"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTurnScreenOn(true)
        setShowWhenLocked(true)
        setContentView(R.layout.activity_alarm_lock_screen)
    }

    fun stopAlarmLockScreen(view: View?) {
        val dismissIntent = Intent(this, AlarmReceiver::class.java)
        dismissIntent.action = AlarmClock.ACTION_DISMISS_ALARM
        dismissIntent.putExtra(Notification.EXTRA_NOTIFICATION_ID, NOTIFICATION_DISMISS)
        sendBroadcast(dismissIntent)
    }
}