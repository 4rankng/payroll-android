package com.payroll.android.ui.admin.employees

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payroll.android.data.remote.dto.*
import com.payroll.android.domain.repository.AdminEmployeeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EmployeeListState(
    val isLoading: Boolean = true,
    val summary: EmployeeSummary? = null,
    val employees: List<AdminEmployee> = emptyList(),
    val search: String = "",
    val statusFilter: String? = null,
    val page: Int = 1,
    val hasMore: Boolean = true,
    val isLoadingMore: Boolean = false,
    val selectedEmployee: AdminEmployee? = null,
    val employeeProjects: List<EmployeeProject> = emptyList(),
    val employeeTimesheetSummary: EmployeeTimesheetSummary? = null,
    val employeePayroll: List<PayrollEntry> = emptyList(),
    val missingBankDetails: List<BankDetail> = emptyList(),
    val showDetail: Boolean = false,
    val showMissingBank: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class EmployeeViewModel @Inject constructor(
    private val employeeRepo: AdminEmployeeRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EmployeeListState())
    val state = _state.asStateFlow()

    init { loadAll() }

    fun loadAll() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            employeeRepo.getSummary().onSuccess { _state.value = _state.value.copy(summary = it) }
            loadEmployees(reset = true)
            loadMissingBank()
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    private suspend fun loadEmployees(reset: Boolean = false) {
        val s = _state.value
        val page = if (reset) 1 else s.page
        if (!reset) _state.value = s.copy(isLoadingMore = true)
        employeeRepo.getEmployees(page, 20, s.search.ifBlank { null }, s.statusFilter, null)
            .onSuccess { result ->
                _state.value = _state.value.copy(
                    employees = if (reset) result.items else s.employees + result.items,
                    page = page + 1,
                    hasMore = page < result.totalPages,
                    isLoadingMore = false
                )
            }
            .onFailure { _state.value = _state.value.copy(isLoadingMore = false) }
    }

    private suspend fun loadMissingBank() {
        employeeRepo.getMissingBankDetails().onSuccess { _state.value = _state.value.copy(missingBankDetails = it) }
    }

    fun onSearch(query: String) {
        _state.value = _state.value.copy(search = query)
        viewModelScope.launch { loadEmployees(reset = true) }
    }

    fun onStatusFilter(status: String?) {
        _state.value = _state.value.copy(statusFilter = status)
        viewModelScope.launch { loadEmployees(reset = true) }
    }

    fun loadMore() { viewModelScope.launch { loadEmployees() } }

    fun selectEmployee(emp: AdminEmployee) {
        _state.value = _state.value.copy(selectedEmployee = emp, showDetail = true)
        viewModelScope.launch {
            employeeRepo.getEmployeeProjects(emp.id).onSuccess { _state.value = _state.value.copy(employeeProjects = it) }
            employeeRepo.getEmployeeTimesheetSummary(emp.id).onSuccess { _state.value = _state.value.copy(employeeTimesheetSummary = it) }
            employeeRepo.getEmployeePayroll(emp.id).onSuccess { _state.value = _state.value.copy(employeePayroll = it) }
        }
    }

    fun dismissDetail() {
        _state.value = _state.value.copy(showDetail = false, selectedEmployee = null, employeeProjects = emptyList(), employeeTimesheetSummary = null, employeePayroll = emptyList())
    }

    fun deleteEmployee(id: Int) {
        viewModelScope.launch {
            employeeRepo.deleteEmployee(id).onSuccess { loadAll() }
        }
    }

    fun toggleMissingBank() {
        _state.value = _state.value.copy(showMissingBank = !_state.value.showMissingBank)
    }
}
