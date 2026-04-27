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

    private val _uiState = MutableStateFlow(DeviceHealthData())
    val uiState: StateFlow<DeviceHealthData> = _uiState.asStateFlow()

    private val _historyState = MutableStateFlow<List<HealthHistory>>(emptyList())
    val historyState: StateFlow<List<HealthHistory>> = _historyState.asStateFlow()

    private val _optimizationState = MutableStateFlow<OptimizationResult?>(null)
    val optimizationState: StateFlow<OptimizationResult?> = _optimizationState.asStateFlow()

    private val _hasUsagePermission = MutableStateFlow(false)
    val hasUsagePermission: StateFlow<Boolean> = _hasUsagePermission.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var autoRefreshJob: Job? = null

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
            // Re-check permission every time (fixes the "already granted but still showing" bug)
            _hasUsagePermission.value = repository.hasUsagePermission()
            repository.loadHistory()
            repository.loadAllData()
            _isRefreshing.value = false
        }
    }

    /** Called from pull-to-refresh gesture */
    fun refresh() {
        if (_isRefreshing.value) return
        loadData()
    }

    /** Called when app comes back to foreground — re-check permissions */
    fun onResume() {
        val newPermState = repository.hasUsagePermission()
        if (newPermState != _hasUsagePermission.value) {
            _hasUsagePermission.value = newPermState
            // If permission was just granted, reload data to get apps
            if (newPermState) loadData()
        }
    }

    /** Auto-refresh every 15 seconds for realtime feel */
    private fun startAutoRefresh() {
        autoRefreshJob?.cancel()
        autoRefreshJob = viewModelScope.launch {
            while (true) {
                delay(15_000L)
                // Silent refresh (don't show loading spinner for auto-refresh)
                _hasUsagePermission.value = repository.hasUsagePermission()
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
            delay(500)
            repository.loadAllData()
        }
    }

    fun resetOptimization() {
        _optimizationState.value = null
    }

    override fun onCleared() {
        super.onCleared()
        autoRefreshJob?.cancel()
    }
}
