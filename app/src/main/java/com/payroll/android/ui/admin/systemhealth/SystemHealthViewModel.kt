package com.payroll.android.ui.admin.systemhealth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payroll.android.data.remote.dto.*
import com.payroll.android.domain.repository.SystemHealthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SystemHealthState(
    val isLoading: Boolean = true,
    val metrics: List<ApiMetric> = emptyList(),
    val recentErrors: List<RecentError> = emptyList(),
    val slowestEndpoints: List<SlowestEndpoint> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class SystemHealthViewModel @Inject constructor(
    private val healthRepo: SystemHealthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SystemHealthState())
    val state = _state.asStateFlow()

    init { loadAll() }

    fun loadAll() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val metrics = healthRepo.getApiMetrics().getOrElse { emptyList() }
            val errors = healthRepo.getRecentErrors().getOrElse { emptyList() }
            val slowest = healthRepo.getSlowestEndpoints().getOrElse { emptyList() }
            _state.value = SystemHealthState(isLoading = false, metrics = metrics, recentErrors = errors, slowestEndpoints = slowest)
        }
    }
}
