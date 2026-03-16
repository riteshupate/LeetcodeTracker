package com.leetcode.tracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leetcode.tracker.api.LeetCodeApi
import com.leetcode.tracker.api.LeetCodeUserData
import com.leetcode.tracker.data.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

sealed class UIState {
    object Idle : UIState()
    object Loading : UIState()
    data class Success(val data: LeetCodeUserData) : UIState()
    data class Error(val message: String) : UIState()
}

class TrackerViewModel(
    private val repository: UserRepository,
    private val api: LeetCodeApi
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<UIState>(UIState.Idle)
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()
    
    private val _refreshInterval = MutableStateFlow(10 * 60 * 1000L) // 10 minutes
    val refreshInterval: StateFlow<Long> = _refreshInterval.asStateFlow()
    
    private val _currentStreak = MutableStateFlow(0)
    val currentStreak: StateFlow<Int> = _currentStreak.asStateFlow()
    
    private val _todaySolved = MutableStateFlow(false)
    val todaySolved: StateFlow<Boolean> = _todaySolved.asStateFlow()
    
    fun retryFetch(username: String) {
    viewModelScope.launch {
        _uiState.update { UIState.Idle }
        delay(500)
        fetchUserData(username)
    }
}
        
        viewModelScope.launch {
            _uiState.update { UIState.Loading }
            
            try {
                repository.saveUsername(username)
                
                val userData = api.getUserSubmissions(username)
                
                if (userData != null) {
                    _currentStreak.update { calculateCurrentStreak(userData.submissionCalendar) }
                    _todaySolved.update { isTodaySolved(userData.submissionCalendar) }
                    _uiState.update { UIState.Success(userData) }
                } else {
                    _uiState.update { 
                        UIState.Error("User not found or profile is private. Check username.")
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    UIState.Error("Error: ${e.localizedMessage ?: "Unknown error"}") 
                }
            }
        }
    }
    
    fun retryFetch(username: String) {
        _uiState.update { UIState.Idle }
        delay(500) // Brief delay before retry
        fetchUserData(username)
    }
    
    private fun calculateCurrentStreak(calendarData: Map<String, Int>): Int {
        var streak = 0
        val calendar = Calendar.getInstance()
        
        while (true) {
            val dateKey = formatDate(calendar)
            
            if ((calendarData[dateKey] ?: 0) > 0) {
                streak++
                calendar.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }
        
        return streak
    }
    
    private fun isTodaySolved(calendarData: Map<String, Int>): Boolean {
        val todayKey = formatDate(Calendar.getInstance())
        return (calendarData[todayKey] ?: 0) > 0
    }
    
    private fun formatDate(calendar: Calendar): String {
        return String.format(
            "%d-%02d-%02d",
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }
}
