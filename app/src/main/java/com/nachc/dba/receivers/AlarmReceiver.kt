package com.nachc.dba.receivers

import android.app.KeyguardManager
import android.app.Notification.EXTRA_NOTIFICATION_ID
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.provider.AlarmClock.ACTION_DISMISS_ALARM
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.nachc.dba.R
import com.nachc.dba.services.LocationService
import com.nachc.dba.services.RingtoneService
import com.nachc.dba.ui.AlarmLockScreenActivity
import com.nachc.dba.ui.MainActivity

class AlarmReceiver: BroadcastReceiver()     {

    private val TAG = "AlarmReceiver"
    private val NOTIFICATION_TITLE = "Arrived to stop"
    private val NOTIFICATION_TEXT = "Tap to dismiss alarm"
    private val NOTIFICATION_DISMISS = "dismiss"
    private val ALARM_CHANNEL_ID = "alarm_channel"
    private val NOTIFICATION_ID = 0

    private var notificationManager: NotificationManagerCompat? = null

    override fun onReceive(context: Context?, intent: Intent?) {

        // check if user is trying to dismiss the notification
        if (intent!!.hasExtra(EXTRA_NOTIFICATION_ID) &&
            intent.getStringExtra(EXTRA_NOTIFICATION_ID)!! == NOTIFICATION_DISMISS) {
            Log.i(TAG, "dismiss alarm")

            // stop the services
            val stopLocationServiceIntent = Intent(context, LocationService::class.java)
            val stopRingtoneServiceIntent = Intent(context, RingtoneService::class.java)
            context!!.stopService(stopLocationServiceIntent)
            context.stopService(stopRingtoneServiceIntent)

            // cancel ongoing notification
            notificationManager = NotificationManagerCompat.from(context)
            notificationManager!!.cancel(NOTIFICATION_ID)

            // go back to the MainActivity
            val backToMainActivityIntent = Intent(context, MainActivity::class.java)
            backToMainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            backToMainActivityIntent.putExtra(NOTIFICATION_DISMISS, true)
            context.startActivity(backToMainActivityIntent)
        }
        // otherwise, user is setting the alarm
        else {
            Log.i(TAG, "Initiate alarm")
            val ringtoneIntent = Intent(context, RingtoneService::class.java)
            context!!.startService(ringtoneIntent)
            createNotification(context)

            // check if the screen is locked or not
            val km = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            if( km.isKeyguardLocked) {
                Log.i(TAG, "screen is locked")

                val fullScreenIntent = Intent(context, AlarmLockScreenActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                val fullScreenPendingIntent = PendingIntent.getActivity(context, 0,
                    fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT)

                val notificationBuilder = NotificationCompat.Builder(context, ALARM_CHANNEL_ID).apply {
                    setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                    setContentTitle(NOTIFICATION_TITLE)
                    setContentText(NOTIFICATION_TEXT)
                    priority = NotificationCompat.PRIORITY_MAX
                    setCategory(NotificationCompat.CATEGORY_ALARM)
                    setFullScreenIntent(fullScreenPendingIntent, true)
                }
                with(NotificationManagerCompat.from(context)) {
                    notify(NOTIFICATION_ID, notificationBuilder.build())
                }
            }
        }
    }

    private fun createNotification(context: Context) {
        // create intent to call AlarmReceiver and provide action to dismiss the alarm
        val dismissIntent= Intent(context, AlarmReceiver::class.java)
        dismissIntent.action = ACTION_DISMISS_ALARM
        dismissIntent.putExtra(EXTRA_NOTIFICATION_ID, NOTIFICATION_DISMISS)
        val dismissPendingIntent: PendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(
            context,
            ALARM_CHANNEL_ID
        ).apply {
            setSmallIcon(R.drawable.ic_notification_bus)
            setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_notification_bus))
            setContentTitle(NOTIFICATION_TITLE)
            setContentText(NOTIFICATION_TEXT)
            setContentIntent(dismissPendingIntent)
            priority = NotificationCompat.PRIORITY_MAX
            setCategory(NotificationCompat.CATEGORY_ALARM)
            addAction(R.drawable.ic_bus_vector, NOTIFICATION_DISMISS, dismissPendingIntent)
        }
        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }
}