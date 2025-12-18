package com.example.devradarapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devradarapp.data.TrendRepository
import com.example.devradarapp.network.TrendKeyword
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TrendViewModel : ViewModel() {
    private val repository = TrendRepository()

    private val _keywords = MutableStateFlow<List<TrendKeyword>>(emptyList())
    val keywords: StateFlow<List<TrendKeyword>> = _keywords.asStateFlow()
    
    // UI State for loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadTrends()
    }

    fun loadTrends() {
        viewModelScope.launch {
            _isLoading.value = true
            val data = repository.getTrends()
            _keywords.value = data
            _isLoading.value = false
        }
    }
}
