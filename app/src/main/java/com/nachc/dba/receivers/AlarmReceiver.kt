package com.nachc.dba.receivers

import android.app.Notification.EXTRA_NOTIFICATION_ID
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.provider.AlarmClock.ACTION_DISMISS_ALARM
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.nachc.dba.R
import com.nachc.dba.services.LocationService
import com.nachc.dba.services.RingtoneService
import com.nachc.dba.ui.MainActivity

class AlarmReceiver: BroadcastReceiver()     {

    /**
     * TODO:
     *  - wakeup lockscreen and allow to dismiss notification from it.
     * */

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
            intent.getStringExtra(EXTRA_NOTIFICATION_ID).equals(NOTIFICATION_DISMISS)) {
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
        )
            .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
            .setContentTitle(NOTIFICATION_TITLE)
            .setContentText(NOTIFICATION_TEXT)
            .setContentIntent(dismissPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(
                R.drawable.ic_launcher_background,
                NOTIFICATION_DISMISS,
                dismissPendingIntent
            )

            notificationManager = NotificationManagerCompat.from(context)
            notificationManager!!.notify(NOTIFICATION_ID, builder.build())
    }
}