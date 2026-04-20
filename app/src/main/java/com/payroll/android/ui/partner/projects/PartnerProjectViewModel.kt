package com.payroll.android.ui.partner.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payroll.android.data.remote.dto.*
import com.payroll.android.domain.repository.ProjectRepository
import com.payroll.android.domain.repository.AdminTimesheetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PartnerProjectState(
    val isLoading: Boolean = true,
    val projects: List<Project> = emptyList(),
    val selectedProject: Project? = null,
    val projectEmployees: List<ProjectEmployee> = emptyList(),
    val projectTimesheets: List<AdminTimesheet> = emptyList(),
    val showDetail: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class PartnerProjectViewModel @Inject constructor(
    private val projectRepo: ProjectRepository,
    private val timesheetRepo: AdminTimesheetRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PartnerProjectState())
    val state = _state.asStateFlow()

    init { loadProjects() }

    fun loadProjects() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            projectRepo.getProjects(1, 100, null, null, true).onSuccess { result ->
                _state.value = _state.value.copy(projects = result.items, isLoading = false)
            }.onFailure { _state.value = _state.value.copy(isLoading = false, error = it.message) }
        }
    }

    fun loadDetail(projectId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(showDetail = true, isLoading = true)
            val project = projectRepo.getProject(projectId).getOrNull()
            val employees = projectRepo.getProjectEmployees(projectId).getOrNull() ?: emptyList()
            val timesheets = timesheetRepo.getTimesheets(1, 50, null, projectId, null, null, null).getOrNull()?.items ?: emptyList()
            _state.value = _state.value.copy(
                selectedProject = project, projectEmployees = employees,
                projectTimesheets = timesheets, isLoading = false
            )
        }
    }

    fun dismissDetail() { _state.value = _state.value.copy(showDetail = false, selectedProject = null) }
}
