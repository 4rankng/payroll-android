package com.payroll.android.ui.partner.timesheets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payroll.android.data.remote.dto.*
import com.payroll.android.domain.repository.AdminTimesheetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PartnerTimesheetState(
    val isLoading: Boolean = true,
    val groupedTimesheets: List<GroupedTimesheet> = emptyList(),
    val summary: AdminTimesheetSummary? = null,
    val error: String? = null
)

@HiltViewModel
class PartnerTimesheetViewModel @Inject constructor(
    private val timesheetRepo: AdminTimesheetRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PartnerTimesheetState())
    val state = _state.asStateFlow()

    init { loadAll() }

    fun loadAll() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            timesheetRepo.getSummary().onSuccess { _state.value = _state.value.copy(summary = it) }
            timesheetRepo.getGroupedTimesheets(null).onSuccess { grouped ->
                _state.value = _state.value.copy(groupedTimesheets = grouped, isLoading = false)
            }.onFailure { _state.value = _state.value.copy(isLoading = false, error = it.message) }
        }
    }
}
