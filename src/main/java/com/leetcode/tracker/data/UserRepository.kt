package com.leetcode.tracker.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private const val USER_PREFERENCES_NAME = "leetcode_tracker_preferences"
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = USER_PREFERENCES_NAME)

object PreferenceKeys {
    val USERNAME = stringPreferencesKey("username")
    val REMINDER_HOUR = intPreferencesKey("reminder_hour")
    val REMINDER_MINUTE = intPreferencesKey("reminder_minute")
    val REMINDER_ENABLED = intPreferencesKey("reminder_enabled")
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
    
    // FIXED: Correctly reads the actual string value from DataStore
    suspend fun getUsername(): String {
        return context.dataStore.data.first()[PreferenceKeys.USERNAME] ?: ""
    }
    
    // FIXED: Correctly reads the actual int value
    suspend fun getReminderHour(): Int {
        return context.dataStore.data.first()[PreferenceKeys.REMINDER_HOUR] ?: 20
    }

    suspend fun getReminderMinute(): Int {
        return context.dataStore.data.first()[PreferenceKeys.REMINDER_MINUTE] ?: 0
    }
    
    suspend fun isReminderEnabled(): Boolean {
        return context.dataStore.data.first()[PreferenceKeys.REMINDER_ENABLED] == 1
    }
}