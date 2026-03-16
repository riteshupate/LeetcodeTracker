package com.leetcode.tracker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.BorderStroke
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.leetcode.tracker.api.LeetCodeUserData
import java.util.Calendar

@Composable
fun TrackerScreen(
    viewModel: TrackerViewModel,
    onSetReminder: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    val currentStreak by viewModel.currentStreak.collectAsState()
    val todaySolved by viewModel.todaySolved.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDFCFF))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            HeaderCard()
        }

        item {
            InputSection(
                username = username,
                onUsernameChange = { username = it },
                isLoading = uiState is UIState.Loading,
                onSaveClick = {
                    viewModel.fetchUserData(username)
                }
            )
        }

        if (uiState is UIState.Success) {
            val data = (uiState as UIState.Success).data

            item {
                StatsSection(
                    data = data,
                    streak = currentStreak,
                    todaySolved = todaySolved
                )
            }

            item {
                HeatmapSection(data.submissionCalendar)
            }

            item {
                ReminderSection(onSetReminder = onSetReminder)
            }
        }

        if (uiState is UIState.Error) {
            item {
                ErrorSection(
                    message = (uiState as UIState.Error).message,
                    onRetry = { viewModel.fetchUserData(username) }
                )
            }
        }

        if (uiState is UIState.Loading) {
            item {
                LoadingSection()
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun HeaderCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A73E8)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "LeetCode Tracker",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Track your coding journey",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
private fun InputSection(
    username: String,
    onUsernameChange: (String) -> Unit,
    isLoading: Boolean,
    onSaveClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Setup",
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFF1A1A1E)
            )

            OutlinedTextField(
                value = username,
                onValueChange = onUsernameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("LeetCode Username") },
                placeholder = { Text("e.g., rpupate") },
                singleLine = true,
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp)
            )

            Button(
                onClick = onSaveClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = username.isNotEmpty() && !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A73E8)),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Filled.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save and Load Data")
                }
            }
        }
    }
}

@Composable
private fun StatsSection(
    data: LeetCodeUserData,
    streak: Int,
    todaySolved: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                "Your Stats",
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFF1A1A1E)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF3F3FD), RoundedCornerShape(12.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatItem(
                    label = "Total Solved",
                    value = data.totalSolved.toString(),
                    backgroundColor = Color(0xFFD3E3FD)
                )
                VerticalDividerCustom()
                StatItem(
                    label = "Streak",
                    value = "$streak days",
                    backgroundColor = Color(0xFFFFE0B2)
                )
                VerticalDividerCustom()
                StatItem(
                    label = "Today",
                    value = if (todaySolved) "✓" else "✗",
                    textColor = if (todaySolved) Color(0xFF39D353) else Color(0xFFFF375F),
                    backgroundColor = Color(0xFFE8F5E9)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DifficultyBadge(
                    label = "Easy",
                    count = data.easyCount,
                    color = Color(0xFF00b8a3),
                    modifier = Modifier.weight(1f)
                )
                DifficultyBadge(
                    label = "Medium",
                    count = data.mediumCount,
                    color = Color(0xFFFFA116),
                    modifier = Modifier.weight(1f)
                )
                DifficultyBadge(
                    label = "Hard",
                    count = data.hardCount,
                    color = Color(0xFFff375f),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun VerticalDividerCustom() {
    VerticalDivider(
        modifier = Modifier.height(60.dp),
        color = Color(0xFFE0E2EC)
    )
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    textColor: Color = Color(0xFF1A73E8),
    backgroundColor: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineSmall, color = textColor)
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color(0xFF666666))
    }
}

@Composable
private fun DifficultyBadge(
    label: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        border = BorderStroke(1.dp, color),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = color)
            Text(count.toString(), style = MaterialTheme.typography.titleSmall, color = color)
        }
    }
}

@Composable
private fun HeatmapSection(calendarData: Map<String, Int>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                "Submission Activity",
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFF1A1A1E)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Last 365 days of problem-solving activity",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF666666)
            )
            Spacer(modifier = Modifier.height(12.dp))
            HeatmapGrid(calendarData)
        }
    }
}

@Composable
private fun HeatmapGrid(calendarData: Map<String, Int>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF9F9F9), RoundedCornerShape(8.dp))
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        // 7 rows for days of week
        repeat(7) { dayOfWeek ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // Show last 12 weeks (12 columns)
                repeat(12) { weekOffset ->
                    val calendar = Calendar.getInstance()
                    calendar.add(Calendar.WEEK_OF_YEAR, -(11 - weekOffset))
                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                    calendar.add(Calendar.DAY_OF_WEEK, dayOfWeek)
                    
                    val dateKey = String.format(
                        "%d-%02d-%02d",
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH) + 1,
                        calendar.get(Calendar.DAY_OF_MONTH)
                    )
                    
                    val count = calendarData[dateKey] ?: 0
                    val color = getHeatmapColor(count)
                    
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(color, RoundedCornerShape(2.dp))
                    )
                }
            }
        }
        
        // Legend
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Less", style = MaterialTheme.typography.labelSmall, color = Color(0xFF666666))
            listOf(0, 1, 2, 3, 4).forEach { i ->
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(getHeatmapColor(i), RoundedCornerShape(1.dp))
                )
            }
            Text("More", style = MaterialTheme.typography.labelSmall, color = Color(0xFF666666))
        }
    }
}

private fun getHeatmapColor(count: Int): Color {
    return when {
        count == 0 -> Color(0xFFEBEDEF)
        count == 1 -> Color(0xFF0e4429)
        count == 2 -> Color(0xFF196f40)
        count == 3 -> Color(0xFF26a641)
        count == 4 -> Color(0xFF39d353)
        else -> Color(0xFF6bff8e)
    }
}

@Composable
private fun ReminderSection(onSetReminder: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8D4EA)),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                "Daily Reminder",
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFF1A1A1E)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Get notified if you haven't solved a problem today",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF666666)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onSetReminder,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7D5260)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.Notifications, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Set Reminder Time")
            }
        }
    }
}

@Composable
private fun ErrorSection(
    message: String,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color(0xFFFF375F))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text("Error", style = MaterialTheme.typography.titleLarge, color = Color(0xFFFF375F))
            Spacer(modifier = Modifier.height(8.dp))
            Text(message, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF666666))
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onRetry,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF375F)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Retry")
            }
        }
    }
}

@Composable
private fun LoadingSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Color(0xFF1A73E8))
    }
}