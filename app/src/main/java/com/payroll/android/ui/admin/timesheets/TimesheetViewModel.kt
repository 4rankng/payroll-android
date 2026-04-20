package com.payroll.android.ui.admin.timesheets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payroll.android.data.remote.dto.*
import com.payroll.android.domain.repository.AdminTimesheetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TimesheetListState(
    val isLoading: Boolean = true,
    val summary: AdminTimesheetSummary? = null,
    val timesheets: List<AdminTimesheet> = emptyList(),
    val search: String = "",
    val statusFilter: String? = null,
    val page: Int = 1,
    val hasMore: Boolean = true,
    val isLoadingMore: Boolean = false,
    val selectedIds: Set<Int> = emptySet(),
    val showDetail: Boolean = false,
    val selectedTimesheet: AdminTimesheet? = null,
    val rejectReason: String = "",
    val showRejectDialog: Boolean = false,
    val rejectTargetId: Int? = null,
    val error: String? = null
)

data class TimesheetFormState(
    val isLoading: Boolean = false,
    val isEditing: Boolean = false,
    val employeeId: String = "",
    val projectId: String = "",
    val date: String = "",
    val hoursWorked: String = "",
    val paytype: String = "regular",
    val payrate: String = "",
    val note: String = "",
    val error: String? = null,
    val success: Boolean = false
)

@HiltViewModel
class AdminTimesheetViewModel @Inject constructor(
    private val timesheetRepo: AdminTimesheetRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TimesheetListState())
    val state = _state.asStateFlow()

    private val _formState = MutableStateFlow(TimesheetFormState())
    val formState = _formState.asStateFlow()

    init { loadAll() }

    fun loadAll() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            timesheetRepo.getSummary().onSuccess { _state.value = _state.value.copy(summary = it) }
            loadTimesheets(reset = true)
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    private suspend fun loadTimesheets(reset: Boolean = false) {
        val s = _state.value
        val page = if (reset) 1 else s.page
        if (!reset) _state.value = s.copy(isLoadingMore = true)
        timesheetRepo.getTimesheets(page, 20, s.statusFilter, null, null, null, null)
            .onSuccess { result ->
                _state.value = _state.value.copy(
                    timesheets = if (reset) result.items else s.timesheets + result.items,
                    page = page + 1, hasMore = page < result.totalPages, isLoadingMore = false
                )
            }.onFailure { _state.value = _state.value.copy(isLoadingMore = false) }
    }

    fun onStatusFilter(status: String?) {
        _state.value = _state.value.copy(statusFilter = status)
        viewModelScope.launch { loadTimesheets(reset = true) }
    }

    fun loadMore() { viewModelScope.launch { loadTimesheets() } }

    fun toggleSelect(id: Int) {
        val selected = _state.value.selectedIds
        _state.value = _state.value.copy(selectedIds = if (selected.contains(id)) selected - id else selected + id)
    }

    fun clearSelection() { _state.value = _state.value.copy(selectedIds = emptySet()) }

    fun showDetail(ts: AdminTimesheet) {
        _state.value = _state.value.copy(showDetail = true, selectedTimesheet = ts)
    }

    fun dismissDetail() {
        _state.value = _state.value.copy(showDetail = false, selectedTimesheet = null)
    }

    fun approveTimesheet(id: Int) {
        viewModelScope.launch {
            timesheetRepo.approveTimesheet(id).onSuccess { loadAll() }
        }
    }

    fun showRejectDialog(id: Int) {
        _state.value = _state.value.copy(showRejectDialog = true, rejectTargetId = id, rejectReason = "")
    }

    fun dismissRejectDialog() {
        _state.value = _state.value.copy(showRejectDialog = false, rejectTargetId = null)
    }

    fun onRejectReasonChange(reason: String) {
        _state.value = _state.value.copy(rejectReason = reason)
    }

    fun confirmReject() {
        val id = _state.value.rejectTargetId ?: return
        viewModelScope.launch {
            timesheetRepo.rejectTimesheet(id, _state.value.rejectReason).onSuccess {
                _state.value = _state.value.copy(showRejectDialog = false)
                loadAll()
            }
        }
    }

    fun bulkApprove() {
        val ids = _state.value.selectedIds.toList()
        if (ids.isEmpty()) return
        viewModelScope.launch {
            timesheetRepo.bulkApprove(ids).onSuccess {
                _state.value = _state.value.copy(selectedIds = emptySet())
                loadAll()
            }
        }
    }

    fun bulkReject() {
        val ids = _state.value.selectedIds.toList()
        if (ids.isEmpty()) return
        viewModelScope.launch {
            timesheetRepo.bulkReject(ids).onSuccess {
                _state.value = _state.value.copy(selectedIds = emptySet())
                loadAll()
            }
        }
    }

    fun approveAll() {
        viewModelScope.launch { timesheetRepo.approveAll().onSuccess { loadAll() } }
    }

    fun deleteTimesheet(id: Int) {
        viewModelScope.launch { timesheetRepo.deleteTimesheet(id).onSuccess { loadAll() } }
    }

    // Form methods
    private var editingTimesheetId: Int = 0

    fun loadForm(id: Int?) {
        if (id == null) { _formState.value = TimesheetFormState(); return }
        editingTimesheetId = id
        viewModelScope.launch {
            _formState.value = TimesheetFormState(isLoading = true)
            timesheetRepo.getTimesheetDetail(id).onSuccess { ts ->
                _formState.value = TimesheetFormState(
                    isEditing = true, isLoading = false,
                    employeeId = ts.employeeId?.toString() ?: "", projectId = ts.projectId?.toString() ?: "",
                    date = ts.date ?: "", hoursWorked = ts.hoursWorked?.toString() ?: "",
                    paytype = ts.paytype ?: "regular", payrate = ts.payrate?.toString() ?: "", note = ts.note ?: ""
                )
            }.onFailure { _formState.value = TimesheetFormState(error = it.message) }
        }
    }

    fun onFormField(field: String, value: String) {
        _formState.value = when (field) {
            "employeeId" -> _formState.value.copy(employeeId = value)
            "projectId" -> _formState.value.copy(projectId = value)
            "date" -> _formState.value.copy(date = value)
            "hoursWorked" -> _formState.value.copy(hoursWorked = value)
            "paytype" -> _formState.value.copy(paytype = value)
            "payrate" -> _formState.value.copy(payrate = value)
            "note" -> _formState.value.copy(note = value)
            else -> _formState.value
        }
    }

    fun saveTimesheet(onSuccess: () -> Unit) {
        val s = _formState.value
        viewModelScope.launch {
            _formState.value = s.copy(isLoading = true, error = null)
            val result = if (s.isEditing) {
                timesheetRepo.updateTimesheet(editingTimesheetId, UpdateTimesheetRequest(
                    employeeId = s.employeeId.toIntOrNull(), projectId = s.projectId.toIntOrNull(),
                    date = s.date.ifBlank { null }, hoursWorked = s.hoursWorked.toDoubleOrNull(),
                    paytype = s.paytype.ifBlank { null }, payrate = s.payrate.toDoubleOrNull(), note = s.note.ifBlank { null }
                ))
            } else {
                timesheetRepo.createTimesheet(CreateTimesheetRequest(
                    employeeId = s.employeeId.toIntOrNull() ?: 0, projectId = s.projectId.toIntOrNull() ?: 0,
                    date = s.date, hoursWorked = s.hoursWorked.toDoubleOrNull() ?: 0.0,
                    paytype = s.paytype.ifBlank { null }, payrate = s.payrate.toDoubleOrNull(), note = s.note.ifBlank { null }
                ))
            }
            result.onSuccess { _formState.value = _formState.value.copy(isLoading = false, success = true); onSuccess() }
                .onFailure { _formState.value = _formState.value.copy(isLoading = false, error = it.message) }
        }
    }
}
