package com.leetcode.tracker

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.leetcode.tracker.api.LeetCodeApi
import com.leetcode.tracker.data.UserRepository
import com.leetcode.tracker.notifications.DailyReminderReceiver
import com.leetcode.tracker.ui.TrackerScreen
import com.leetcode.tracker.ui.TrackerViewModel
import com.leetcode.tracker.ui.TrackerViewModelFactory
import com.leetcode.tracker.ui.theme.LeetCodeTrackerTheme
import java.util.Calendar

class MainActivity : ComponentActivity() {
    
    private lateinit var viewModel: TrackerViewModel
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted - notifications will work
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize repository and API
        val repository = UserRepository(this)
        val api = LeetCodeApi()
        
        // Initialize ViewModel
        viewModel = ViewModelProvider(
            this,
            TrackerViewModelFactory(repository, api)
        ).get(TrackerViewModel::class.java)
        
        // Create notification channel
        createNotificationChannel()
        
        // Request notification permission for Android 13+
        requestNotificationPermission()
        
        setContent {
            LeetCodeTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TrackerScreen(
                        viewModel = viewModel,
                        onSetReminder = { showTimePickerDialog() }
                    )
                }
            }
        }
    }
    
    private fun showTimePickerDialog() {
        val hour = 20
        val minute = 0
        
        TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            scheduleReminder(selectedHour, selectedMinute)
        }, hour, minute, true).show()
    }
    
    private fun scheduleReminder(hour: Int, minute: Int) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, DailyReminderReceiver::class.java)
        intent.action = "com.leetcode.tracker.REMINDER_ALARM"
        
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            
            if (timeInMillis < System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        
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
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "LeetCode Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Daily reminders to solve LeetCode problems"
                enableVibration(true)
                setShowBadge(true)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
    
    companion object {
        const val NOTIFICATION_CHANNEL_ID = "leetcode_reminders"
    }
}