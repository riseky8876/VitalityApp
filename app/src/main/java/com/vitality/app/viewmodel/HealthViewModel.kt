package com.vitality.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vitality.app.data.model.DeviceHealthData
import com.vitality.app.data.model.HealthHistory
import com.vitality.app.data.model.OptimizationResult
import com.vitality.app.data.repository.HealthRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class HealthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = HealthRepository(application)

    // Health data state
    private val _uiState = MutableStateFlow(DeviceHealthData())
    val uiState: StateFlow<DeviceHealthData> = _uiState.asStateFlow()

    // History state
    private val _historyState = MutableStateFlow<List<HealthHistory>>(emptyList())
    val historyState: StateFlow<List<HealthHistory>> = _historyState.asStateFlow()

    // Optimization state
    private val _optimizationState = MutableStateFlow<OptimizationResult?>(null)
    val optimizationState: StateFlow<OptimizationResult?> = _optimizationState.asStateFlow()

    // Permission state
    private val _hasUsagePermission = MutableStateFlow(false)
    val hasUsagePermission: StateFlow<Boolean> = _hasUsagePermission.asStateFlow()

    // Refresh state
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var refreshJob: Job? = null

    init {
        observeData()
        loadData()
        startAutoRefresh()
    }

    private fun observeData() {
        repository.healthData
            .onEach { data -> _uiState.value = data }
            .launchIn(viewModelScope)

        repository.historyData
            .onEach { history -> _historyState.value = history }
            .launchIn(viewModelScope)
    }

    fun loadData() {
        viewModelScope.launch {
            _isRefreshing.value = true
            _hasUsagePermission.value = repository.hasUsagePermission()
            repository.loadHistory()
            repository.loadAllData()
            _isRefreshing.value = false
        }
    }

    fun refresh() {
        loadData()
    }

    // Auto-refresh every 30 seconds
    private fun startAutoRefresh() {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            while (true) {
                delay(30_000)
                repository.loadAllData()
            }
        }
    }

    fun runOptimization() {
        if (_optimizationState.value?.isCompleted == false &&
            _optimizationState.value != null) return

        viewModelScope.launch {
            _optimizationState.value = null
            repository.runOptimization { result ->
                _optimizationState.value = result
            }
            // Refresh data after optimization
            delay(500)
            repository.loadAllData()
        }
    }

    fun resetOptimization() {
        _optimizationState.value = null
    }

    override fun onCleared() {
        super.onCleared()
        refreshJob?.cancel()
    }
}
