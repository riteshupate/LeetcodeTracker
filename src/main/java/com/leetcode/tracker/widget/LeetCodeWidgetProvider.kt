package com.leetcode.tracker.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import com.leetcode.tracker.MainActivity
import com.leetcode.tracker.R
import com.leetcode.tracker.api.LeetCodeApi
import com.leetcode.tracker.data.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class LeetCodeWidgetProvider : AppWidgetProvider() {

    private val tag = "LeetCodeWidget"

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        scheduleWidgetRefresh(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        cancelWidgetRefresh(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == "com.leetcode.tracker.REFRESH_WIDGET") {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                ComponentName(context, LeetCodeWidgetProvider::class.java)
            )
            onUpdate(context, appWidgetManager, appWidgetIds)
        }
    }

    companion object {
        
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_layout)
            
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // FIXED: Fetch data dynamically from DataStore
                    val repository = UserRepository(context)
                    val username = repository.getUsername()

                    if (username.isNotEmpty()) {
                        val api = LeetCodeApi()
                        val userData = api.getUserSubmissions(username)

                        if (userData != null) {
                            val streak = calculateStreak(userData.submissionCalendar)
                            val todayKey = getTodayKey()
                            // FIXED: Operator precedence - wrap ?: 0 in parentheses
                            val solvedToday = (userData.submissionCalendar[todayKey] ?: 0) > 0

                            views.setTextViewText(R.id.widget_streak, "$streak")
                            views.setTextViewText(R.id.widget_solved, "${userData.totalSolved}")
                            views.setTextViewText(
                                R.id.widget_status,
                                if (solvedToday) "✓ Completed" else "⚠ Not yet"
                            )
                            views.setTextColor(
                                R.id.widget_status,
                                if (solvedToday) Color.parseColor("#39D353") else Color.parseColor("#FFA726")
                            )

                            val heatmapBitmap = drawHeatmap(userData.submissionCalendar)
                            views.setImageViewBitmap(R.id.widget_heatmap, heatmapBitmap)

                            appWidgetManager.updateAppWidget(appWidgetId, views)
                            Log.d(tag, "Widget updated successfully for user: $username")
                        } else {
                            Log.e(tag, "Failed to fetch user data")
                            views.setTextViewText(R.id.widget_status, "Error loading data")
                            appWidgetManager.updateAppWidget(appWidgetId, views)
                        }
                    } else {
                        views.setTextViewText(R.id.widget_streak, "0")
                        views.setTextViewText(R.id.widget_solved, "0")
                        views.setTextViewText(R.id.widget_status, "Set username")
                        appWidgetManager.updateAppWidget(appWidgetId, views)
                        Log.d(tag, "Username not set")
                    }
                } catch (e: Exception) {
                    Log.e(tag, "Exception updating widget", e)
                }
            }
        }

        private fun drawHeatmap(data: Map<String, Int>): Bitmap {
            val weeksToShow = 20
            val cellSize = 16f
            val spacing = 3f
            val daysInWeek = 7
            val monthLabelHeight = 20f

            var currentX = 0f
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.WEEK_OF_YEAR, -(weeksToShow - 1))
            while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                calendar.add(Calendar.DAY_OF_YEAR, -1)
            }

            val startCal = calendar.clone() as Calendar
            var prevMonth = -1
            val today = Calendar.getInstance()

            val simCal = startCal.clone() as Calendar
            while (!simCal.after(today)) {
                val month = simCal.get(Calendar.MONTH)
                val dayOfWeek = simCal.get(Calendar.DAY_OF_WEEK) - 1

                if (dayOfWeek == 0) {
                    currentX += cellSize + spacing
                }

                if (month != prevMonth && prevMonth != -1) {
                    currentX += cellSize + spacing
                }
                prevMonth = month
                simCal.add(Calendar.DAY_OF_YEAR, 1)
            }

            val width = currentX.toInt() + 16
            val height = ((cellSize + spacing) * daysInWeek + monthLabelHeight).toInt()

            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            val paint = Paint()
            val textPaint = Paint().apply {
                color = Color.parseColor("#8B949E")
                textSize = 12f
                typeface = Typeface.DEFAULT
                isAntiAlias = true
            }

            currentX = 0f
            prevMonth = -1
            val drawCal = startCal.clone() as Calendar

            while (!drawCal.after(today)) {
                val month = drawCal.get(Calendar.MONTH)
                val dayOfWeek = drawCal.get(Calendar.DAY_OF_WEEK) - 1

                if (dayOfWeek == 0) {
                    currentX += cellSize + spacing
                }

                if (month != prevMonth && prevMonth != -1) {
                    currentX += cellSize + spacing
                    val monthName = drawCal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US)
                    canvas.drawText(monthName ?: "", currentX, height - 2f, textPaint)
                }
                prevMonth = month

                val dateKey = String.format(
                    "%d-%02d-%02d",
                    drawCal.get(Calendar.YEAR),
                    drawCal.get(Calendar.MONTH) + 1,
                    drawCal.get(Calendar.DAY_OF_MONTH)
                )

                val count = data[dateKey] ?: 0
                paint.color = getHeatmapColor(count)

                val top = dayOfWeek * (cellSize + spacing)
                canvas.drawRoundRect(
                    currentX, top,
                    currentX + cellSize, top + cellSize,
                    2f, 2f,
                    paint
                )

                drawCal.add(Calendar.DAY_OF_YEAR, 1)
            }

            return bitmap
        }

        private fun getHeatmapColor(count: Int): Int {
            return when {
                count == 0 -> Color.parseColor("#2D333B")
                count == 1 -> Color.parseColor("#0e4429")
                count == 2 -> Color.parseColor("#196f40")
                count == 3 -> Color.parseColor("#26a641")
                count == 4 -> Color.parseColor("#39d353")
                else -> Color.parseColor("#6bff8e")
            }
        }

        private fun calculateStreak(data: Map<String, Int>): Int {
            var streak = 0
            val calendar = Calendar.getInstance()

            val todayKey = String.format(
                "%d-%02d-%02d",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            if ((data[todayKey] ?: 0) > 0) {
                streak++
            }
            
            calendar.add(Calendar.DAY_OF_YEAR, -1)

            while (true) {
                val dateKey = String.format(
                    "%d-%02d-%02d",
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH)
                )

                if ((data[dateKey] ?: 0) > 0) {
                    streak++
                    calendar.add(Calendar.DAY_OF_YEAR, -1)
                } else {
                    break
                }
            }

            return streak
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

        private fun scheduleWidgetRefresh(context: Context) {
            val intent = Intent(context, LeetCodeWidgetProvider::class.java).apply {
                action = "com.leetcode.tracker.REFRESH_WIDGET"
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val interval = 10 * 60 * 1000L // 10 minutes
            
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC,
                            System.currentTimeMillis() + interval,
                            pendingIntent
                        )
                    } else {
                        alarmManager.setAndAllowWhileIdle(
                            AlarmManager.RTC,
                            System.currentTimeMillis() + interval,
                            pendingIntent
                        )
                    }
                } else {
                    @Suppress("DEPRECATION")
                    alarmManager.setRepeating(
                        AlarmManager.RTC,
                        System.currentTimeMillis() + interval,
                        interval,
                        pendingIntent
                    )
                }
                Log.d(tag, "Widget refresh scheduled every 10 minutes")
            } catch (e: SecurityException) {
                Log.e(tag, "SecurityException scheduling widget refresh", e)
            }
        }

        private fun cancelWidgetRefresh(context: Context) {
            val intent = Intent(context, LeetCodeWidgetProvider::class.java).apply {
                action = "com.leetcode.tracker.REFRESH_WIDGET"
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            
            try {
                alarmManager.cancel(pendingIntent)
                Log.d(tag, "Widget refresh cancelled")
            } catch (e: Exception) {
                Log.e(tag, "Exception cancelling widget refresh", e)
            }
        }
    }
}