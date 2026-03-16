package com.leetcode.tracker.notifications

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.leetcode.tracker.MainActivity
import com.leetcode.tracker.api.LeetCodeApi
import com.leetcode.tracker.data.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class DailyReminderReceiver : BroadcastReceiver() {
    
    private val tag = "DailyReminder"
    
    override fun onReceive(context: Context, intent: Intent) {
        val result = goAsync()
        
        CoroutineScope(Dispatchers.Default).launch {
            try {
                // FIXED: Now properly uses the UserRepository to fetch the real data.
                val repository = UserRepository(context)
                val username = repository.getUsername()
                
                if (username.isNotEmpty()) {
                    val api = LeetCodeApi()
                    val userData = api.getUserSubmissions(username)
                    
                    if (userData != null) {
                        val todayKey = getTodayKey()
                        val solvedToday = userData.submissionCalendar[todayKey] ?: 0
                        
                        if (solvedToday == 0) {
                            showNotification(context)
                            Log.d(tag, "Reminder notification shown for user: $username")
                        } else {
                            Log.d(tag, "User already solved today, skipping notification")
                        }
                    } else {
                        Log.w(tag, "Failed to fetch user data")
                    }
                } else {
                    Log.d(tag, "Username not set, skipping reminder")
                }
                
                // Reschedule for next day
                val hour = repository.getReminderHour()
                val minute = repository.getReminderMinute()
                rescheduleReminder(context, hour, minute)
            } catch (e: Exception) {
                Log.e(tag, "Exception in DailyReminderReceiver", e)
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
            .setVibrate(longArrayOf(0, 500, 250, 500))
            .build()
        
        notificationManager.notify(1, notification)
    }
    
    private fun rescheduleReminder(context: Context, hour: Int, minute: Int) {
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
            Log.d(tag, "Reminder rescheduled for $hour:$minute")
        } catch (e: SecurityException) {
            Log.e(tag, "SecurityException scheduling alarm", e)
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