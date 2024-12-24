package com.dicoding.courseschedule.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.dicoding.courseschedule.R
import com.dicoding.courseschedule.data.Course
import com.dicoding.courseschedule.data.DataRepository
import com.dicoding.courseschedule.ui.home.HomeActivity
import com.dicoding.courseschedule.util.ID_REPEATING
import com.dicoding.courseschedule.util.NOTIFICATION_CHANNEL_ID
import com.dicoding.courseschedule.util.NOTIFICATION_CHANNEL_NAME
import com.dicoding.courseschedule.util.NOTIFICATION_ID
import com.dicoding.courseschedule.util.executeThread
import java.util.Calendar

class DailyReminder : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        executeThread {
            val repository = DataRepository.getInstance(context)
            val courses = repository?.getTodaySchedule()

            courses?.let {
                if (it.isNotEmpty()) showNotification(context, it)
            }
        }
    }

    //TODO 12 : Implement daily reminder for every 06.00 a.m using AlarmManager
    fun setDailyReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, DailyReminder::class.java)

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 6)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        val pendingIntent =
            PendingIntent.getBroadcast(context, ID_REPEATING, intent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    fun cancelAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, DailyReminder::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ID_REPEATING,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        if (pendingIntent != null) {
            pendingIntent.cancel()
            alarmManager.cancel(pendingIntent)
        }
    }

    private fun getPendingIntent(context: Context): PendingIntent? {
        val intent = Intent(context, HomeActivity::class.java)

        return TaskStackBuilder.create(context).run {
            addNextIntent(intent)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getPendingIntent(
                    NOTIFICATION_ID,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            } else {
                getPendingIntent(NOTIFICATION_ID, PendingIntent.FLAG_UPDATE_CURRENT)
            }
        }
    }

    private fun showNotification(context: Context, content: List<Course>) {
        //TODO 13 : Show today schedules in inbox style notification & open HomeActivity when notification tapped
        val notificationStyle = NotificationCompat.InboxStyle()
        val timeString = context.resources.getString(R.string.notification_message_format)
        content.forEach {
            val courseData = String.format(timeString, it.startTime, it.endTime, it.courseName)
            notificationStyle.addLine(courseData)
        }

        val notificationManagerCompat =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(context.resources.getString(R.string.today_schedule))
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setSound(alarmSound)
            .setContentIntent(getPendingIntent(context))
            .setStyle(notificationStyle)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableVibration(true)
                vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)
            }
            builder.setChannelId(NOTIFICATION_CHANNEL_ID)
            notificationManagerCompat.createNotificationChannel(channel)
        }

        val notification = builder.build()
        notificationManagerCompat.notify(NOTIFICATION_ID, notification)
    }
}