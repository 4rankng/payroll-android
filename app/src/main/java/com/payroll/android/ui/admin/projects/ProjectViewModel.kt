package com.payroll.android.ui.admin.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payroll.android.data.remote.dto.*
import com.payroll.android.domain.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProjectListState(
    val isLoading: Boolean = true,
    val summary: ProjectSummary? = null,
    val projects: List<Project> = emptyList(),
    val search: String = "",
    val statusFilter: String? = null,
    val page: Int = 1,
    val hasMore: Boolean = true,
    val isLoadingMore: Boolean = false,
    val error: String? = null
)

data class ProjectDetailState(
    val project: Project? = null,
    val employees: List<ProjectEmployee> = emptyList(),
    val financial: ProjectFinancial? = null,
    val payrates: List<Payrate> = emptyList(),
    val isLoading: Boolean = true
)

data class ProjectFormState(
    val isLoading: Boolean = false,
    val isEditing: Boolean = false,
    val name: String = "",
    val code: String = "",
    val clientName: String = "",
    val description: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val status: String = "active",
    val error: String? = null,
    val success: Boolean = false
)

@HiltViewModel
class ProjectViewModel @Inject constructor(
    private val projectRepo: ProjectRepository
) : ViewModel() {

    private val _listState = MutableStateFlow(ProjectListState())
    val listState = _listState.asStateFlow()

    private val _detailState = MutableStateFlow(ProjectDetailState())
    val detailState = _detailState.asStateFlow()

    private val _formState = MutableStateFlow(ProjectFormState())
    val formState = _formState.asStateFlow()

    init { loadProjects() }

    fun loadProjects() {
        viewModelScope.launch {
            _listState.value = _listState.value.copy(isLoading = true)
            projectRepo.getSummary().onSuccess { _listState.value = _listState.value.copy(summary = it) }
            loadProjectList(reset = true)
            _listState.value = _listState.value.copy(isLoading = false)
        }
    }

    private suspend fun loadProjectList(reset: Boolean = false) {
        val s = _listState.value
        val page = if (reset) 1 else s.page
        if (!reset) _listState.value = s.copy(isLoadingMore = true)
        projectRepo.getProjects(page, 20, s.search.ifBlank { null }, s.statusFilter, null)
            .onSuccess { result ->
                _listState.value = _listState.value.copy(
                    projects = if (reset) result.items else s.projects + result.items,
                    page = page + 1, hasMore = page < result.totalPages, isLoadingMore = false
                )
            }.onFailure { _listState.value = _listState.value.copy(isLoadingMore = false) }
    }

    fun onSearch(query: String) {
        _listState.value = _listState.value.copy(search = query)
        viewModelScope.launch { loadProjectList(reset = true) }
    }

    fun onStatusFilter(status: String?) {
        _listState.value = _listState.value.copy(statusFilter = status)
        viewModelScope.launch { loadProjectList(reset = true) }
    }

    fun loadMore() { viewModelScope.launch { loadProjectList() } }

    fun loadDetail(id: Int) {
        viewModelScope.launch {
            _detailState.value = ProjectDetailState(isLoading = true)
            val project = projectRepo.getProject(id).getOrNull()
            val employees = projectRepo.getProjectEmployees(id).getOrNull() ?: emptyList()
            val financial = projectRepo.getProjectFinancial(id).getOrNull()
            _detailState.value = ProjectDetailState(project = project, employees = employees, financial = financial, isLoading = false)
        }
    }

    private var editingProjectId: Int = 0

    fun loadForm(id: Int?) {
        if (id == null) {
            _formState.value = ProjectFormState()
            return
        }
        editingProjectId = id
        viewModelScope.launch {
            _formState.value = ProjectFormState(isLoading = true)
            projectRepo.getProject(id).onSuccess { p ->
                _formState.value = ProjectFormState(
                    isEditing = true, isLoading = false,
                    name = p.name ?: "", code = p.code ?: "", clientName = p.clientName ?: "",
                    description = p.description ?: "", startDate = p.startDate ?: "",
                    endDate = p.endDate ?: "", status = p.status ?: "active"
                )
            }.onFailure { _formState.value = ProjectFormState(error = it.message) }
        }
    }

    fun onFormField(field: String, value: String) {
        _formState.value = when (field) {
            "name" -> _formState.value.copy(name = value)
            "code" -> _formState.value.copy(code = value)
            "clientName" -> _formState.value.copy(clientName = value)
            "description" -> _formState.value.copy(description = value)
            "startDate" -> _formState.value.copy(startDate = value)
            "endDate" -> _formState.value.copy(endDate = value)
            "status" -> _formState.value.copy(status = value)
            else -> _formState.value
        }
    }

    fun saveProject(onSuccess: () -> Unit) {
        val s = _formState.value
        if (s.name.isBlank()) { _formState.value = s.copy(error = "Vui lòng nhập tên dự án"); return }
        viewModelScope.launch {
            _formState.value = s.copy(isLoading = true, error = null)
            val request = if (s.isEditing) {
                UpdateProjectRequest(name = s.name, code = s.code.ifBlank { null }, clientName = s.clientName.ifBlank { null },
                    description = s.description.ifBlank { null }, startDate = s.startDate.ifBlank { null }, endDate = s.endDate.ifBlank { null }, status = s.status)
            } else {
                CreateProjectRequest(name = s.name, code = s.code.ifBlank { null }, clientName = s.clientName.ifBlank { null },
                    description = s.description.ifBlank { null }, startDate = s.startDate.ifBlank { null }, endDate = s.endDate.ifBlank { null }, status = s.status)
            }
            val result = if (s.isEditing) projectRepo.updateProject(editingProjectId, request as UpdateProjectRequest) else projectRepo.createProject(request as CreateProjectRequest)
            result.onSuccess { _formState.value = _formState.value.copy(isLoading = false, success = true); onSuccess() }
                .onFailure { _formState.value = _formState.value.copy(isLoading = false, error = it.message) }
        }
    }

    fun removeEmployeeFromProject(projectId: Int, employeeId: Int) {
        viewModelScope.launch {
            projectRepo.removeEmployee(projectId, AssignEmployeeRequest(employeeId))
            loadDetail(projectId)
        }
    }

    fun assignEmployeeToProject(projectId: Int, employeeId: Int) {
        viewModelScope.launch {
            projectRepo.assignEmployee(projectId, AssignEmployeeRequest(employeeId))
            loadDetail(projectId)
        }
    }
}
