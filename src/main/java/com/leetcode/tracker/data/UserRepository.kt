package com.leetcode.tracker.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val USER_PREFERENCES_NAME = "leetcode_tracker_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = USER_PREFERENCES_NAME)

object PreferenceKeys {
    val USERNAME = stringPreferencesKey("username")
    val REMINDER_HOUR = intPreferencesKey("reminder_hour")
    val REMINDER_MINUTE = intPreferencesKey("reminder_minute")
    val REMINDER_ENABLED = intPreferencesKey("reminder_enabled") // 0 = false, 1 = true
    val LAST_FETCH_TIME = stringPreferencesKey("last_fetch_time")
}

class UserRepository(private val context: Context) {
    
    val usernameFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.USERNAME] ?: ""
    }
    
    val reminderHourFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.REMINDER_HOUR] ?: 20
    }
    
    val reminderMinuteFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.REMINDER_MINUTE] ?: 0
    }
    
    val reminderEnabledFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.REMINDER_ENABLED] == 1
    }
    
    suspend fun saveUsername(username: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.USERNAME] = username
        }
    }
    
    suspend fun saveReminderTime(hour: Int, minute: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.REMINDER_HOUR] = hour
            preferences[PreferenceKeys.REMINDER_MINUTE] = minute
            preferences[PreferenceKeys.REMINDER_ENABLED] = 1
        }
    }
    
    suspend fun disableReminder() {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.REMINDER_ENABLED] = 0
        }
    }
    
    suspend fun getUsername(): String {
        return context.dataStore.data.map { preferences ->
            preferences[PreferenceKeys.USERNAME] ?: ""
        }.map { it }.also { flow ->
            // This is a one-time read, using the flow once
        }.toString()
    }
    
    suspend fun getReminderHour(): Int {
        return context.dataStore.data.map { preferences ->
            preferences[PreferenceKeys.REMINDER_HOUR] ?: 20
        }.toString().toIntOrNull() ?: 20
    }
}
