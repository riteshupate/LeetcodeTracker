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

    private val _refreshInterval = MutableStateFlow(10 * 60 * 1000L)
    val refreshInterval: StateFlow<Long> = _refreshInterval.asStateFlow()

    private val _currentStreak = MutableStateFlow(0)
    val currentStreak: StateFlow<Int> = _currentStreak.asStateFlow()

    private val _todaySolved = MutableStateFlow(false)
    val todaySolved: StateFlow<Boolean> = _todaySolved.asStateFlow()

    fun fetchUserData(username: String) {
        if (username.isBlank()) {
            _uiState.update { UIState.Error("Username cannot be empty") }
            return
        }

        viewModelScope.launch {
            _uiState.update { UIState.Loading }

            try {
                repository.saveUsername(username)

                val userData = api.getUserSubmissions(username)

                if (userData != null) {
                    _currentStreak.update { calculateCurrentStreak(userData.submis