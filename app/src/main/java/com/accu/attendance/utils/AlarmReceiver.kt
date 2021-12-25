package com.accu.attendance.utils

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat
import com.accu.attendance.R
import com.accu.attendance.activity.SplashActivity
import java.util.*

/**
 * Created by neeraj on 27/2/18.
 */

class AlarmReceiver : BroadcastReceiver() {

    lateinit var store: PrefStore

    override fun onReceive(context: Context, intent: Intent) {
        Utils.debugLog("Alarm", "Alarm Received")
        store = PrefStore(context)
        generateNotification(context, intent)
    }

    private fun generateNotification(context: Context, intent: Intent) {
        val snoozeIntent = Intent(context, AlarmReceiver::class.java)
        snoozeIntent.putExtra("snooze", true)
        val snoozePendingIntent = PendingIntent.getBroadcast(context, 0, snoozeIntent, 0)

        val mNotificationManager = context
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (intent.getBooleanExtra("snooze", false)) {
            Utils.debugLog("Alarm", "Setting Alarm again")
            setAlarm(context)
            mNotificationManager.cancel(0)
        } else {
            val builder = NotificationCompat.Builder(context, "my_channel")
            builder.setSmallIcon(R.mipmap.ic_logo_trans)
            builder.setContentTitle(context.getString(R.string.app_name))
            builder.setContentText("Did You forgot to signout for the day?")
            builder.priority = NotificationCompat.PRIORITY_HIGH
            builder.setAutoCancel(true)
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            builder.setStyle(NotificationCompat.BigTextStyle().bigText("Did You forgot to signout for the day?"))
            builder.addAction(0, "Snooze (" + store.getInt(Const.Settings.SNOOZE_M, 10) + "m)", snoozePendingIntent)

            val notiIntent = Intent(context, SplashActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, notiIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            builder.setContentIntent(pendingIntent)
            mNotificationManager.notify(0, builder.build())

        }
    }

    private fun setAlarm(context: Context) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 1, intent, 0)
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().timeInMillis + store.getInt(Const.Settings.SNOOZE_M, 10) * 60 * 1000, pendingIntent)
    }
}
