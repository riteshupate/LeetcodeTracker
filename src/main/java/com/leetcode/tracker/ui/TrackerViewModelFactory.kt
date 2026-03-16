package com.leetcode.tracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.leetcode.tracker.api.LeetCodeApi
import com.leetcode.tracker.data.UserRepository

class TrackerViewModelFactory(
    private val repository: UserRepository,
    private val api: LeetCodeApi
) : ViewModelProvider.Factory {
    
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrackerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TrackerViewModel(repository, api) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}