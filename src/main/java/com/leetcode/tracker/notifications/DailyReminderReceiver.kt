package com.leetcode.tracker.notifications

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.leetcode.tracker.MainActivity
import com.leetcode.tracker.R
import com.leetcode.tracker.api.LeetCodeApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class DailyReminderReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        // Get goAsync to handle long-running tasks
        val result = goAsync()
        
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val sharedPrefs = context.getSharedPreferences("leetcode_tracker", Context.MODE_PRIVATE)
                val username = sharedPrefs.getString("username", "") ?: ""
                
                if (username.isNotEmpty()) {
                    val api = LeetCodeApi()
                    val userData = api.getUserSubmissions(username)
                    
                    if (userData != null) {
                        val todayKey = getTodayKey()
                        val solvedToday = userData.submissionCalendar[todayKey] ?: 0
                        
                        if (solvedToday == 0) {
                            showNotification(context)
                        }
                    }
                }
                
                // Reschedule for next day
                rescheduleReminder(context, sharedPrefs)
            } finally {
                result.finish()
            }
        }
    }
    
    private fun showNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, MainActivity.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("LeetCode Reminder")
            .setContentText("Don't forget to solve a problem today! Keep your streak alive!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        notificationManager.notify(1, notification)
    }
    
    private fun rescheduleReminder(context: Context, sharedPrefs: android.content.SharedPreferences) {
        val hour = sharedPrefs.getInt("reminder_hour", 20)
        val minute = sharedPrefs.getInt("reminder_minute", 0)
        
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            add(Calendar.DAY_OF_YEAR, 1)
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, DailyReminderReceiver::class.java)
        intent.action = "com.leetcode.tracker.REMINDER_ALARM"
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                }
            } else {
                @Suppress("DEPRECATION")
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
    
    private fun getTodayKey(): String {
        val calendar = Calendar.getInstance()
        return String.format(
            "%d-%02d-%02d",
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }
}
