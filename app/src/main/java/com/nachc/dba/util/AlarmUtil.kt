package com.nachc.dba.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.nachc.dba.receivers.AlarmReceiver

fun startAlarm(context: Context, alarmDelay: Int) {
    val alarmIntent = Intent(context, AlarmReceiver::class.java)
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0)
    val alarmClockInfo: AlarmManager.AlarmClockInfo = AlarmManager.AlarmClockInfo(
        System.currentTimeMillis() + alarmDelay,
        pendingIntent
    )
    alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
}