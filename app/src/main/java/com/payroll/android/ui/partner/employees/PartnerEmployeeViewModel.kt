package com.payroll.android.ui.partner.employees

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payroll.android.data.remote.dto.*
import com.payroll.android.domain.repository.AdminEmployeeRepository
import com.payroll.android.domain.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PartnerEmployeeState(
    val isLoading: Boolean = true,
    val employees: List<AdminEmployee> = emptyList(),
    val projects: List<Project> = emptyList(),
    val search: String = "",
    val selectedProjectId: Int? = null,
    val error: String? = null
)

@HiltViewModel
class PartnerEmployeeViewModel @Inject constructor(
    private val employeeRepo: AdminEmployeeRepository,
    private val projectRepo: ProjectRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PartnerEmployeeState())
    val state = _state.asStateFlow()

    init { loadAll() }

    fun loadAll() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val projects = projectRepo.getProjects(1, 100, null, null, true).getOrNull()?.items ?: emptyList()
            val employees = employeeRepo.getEmployees(1, 100, null, null, null).getOrNull()?.items ?: emptyList()
            _state.value = _state.value.copy(projects = projects, employees = employees, isLoading = false)
        }
    }

    fun onSearch(query: String) { _state.value = _state.value.copy(search = query) }
    fun onProjectFilter(projectId: Int?) { _state.value = _state.value.copy(selectedProjectId = projectId); viewModelScope.launch { loadEmployeesForProject(projectId) } }

    private suspend fun loadEmployeesForProject(projectId: Int?) {
        _state.value = _state.value.copy(isLoading = true)
        employeeRepo.getEmployees(1, 100, _state.value.search.ifBlank { null }, null, projectId)
            .onSuccess { _state.value = _state.value.copy(employees = it.items, isLoading = false) }
            .onFailure { _state.value = _state.value.copy(isLoading = false) }
    }
}
