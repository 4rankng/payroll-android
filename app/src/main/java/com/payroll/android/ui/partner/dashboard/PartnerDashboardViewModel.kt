package com.payroll.android.ui.partner.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payroll.android.data.remote.dto.*
import com.payroll.android.domain.repository.DashboardRepository
import com.payroll.android.domain.repository.ProjectRepository
import com.payroll.android.domain.repository.AdminTimesheetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PartnerDashboardState(
    val isLoading: Boolean = true,
    val summary: PartnerSummary? = null,
    val projects: List<Project> = emptyList(),
    val timesheetStatus: Triple<Int, Int, Int> = Triple(0, 0, 0), // pending, approved, rejected
    val error: String? = null
)

@HiltViewModel
class PartnerDashboardViewModel @Inject constructor(
    private val dashboardRepo: DashboardRepository,
    private val projectRepo: ProjectRepository,
    private val timesheetRepo: AdminTimesheetRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PartnerDashboardState())
    val state = _state.asStateFlow()

    init { loadAll() }

    fun loadAll() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val summary = dashboardRepo.getPartnerSummary().getOrNull()
            val projects = projectRepo.getProjects(1, 100, null, null, true).getOrNull()?.items ?: emptyList()
            val tsSummary = timesheetRepo.getSummary().getOrNull()
            _state.value = PartnerDashboardState(
                isLoading = false,
                summary = summary,
                projects = projects,
                timesheetStatus = Triple(tsSummary?.pending ?: 0, tsSummary?.approved ?: 0, tsSummary?.rejected ?: 0)
            )
        }
    }
}
