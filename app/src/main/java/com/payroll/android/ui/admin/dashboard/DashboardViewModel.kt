package com.payroll.android.ui.admin.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payroll.android.data.remote.dto.*
import com.payroll.android.domain.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardState(
    val isLoading: Boolean = true,
    val summary: DashboardSummary? = null,
    val recentActivities: List<RecentActivity> = emptyList(),
    val projectProfitability: List<ProjectProfitability> = emptyList(),
    val salaryDistribution: List<SalaryDistribution> = emptyList(),
    val monthlyFinancials: List<MonthlyFinancial> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val dashboardRepo: DashboardRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()

    init { loadAll() }

    fun loadAll() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            var error: String? = null
            val summary = dashboardRepo.getSummary().getOrElse { error = it.message; null }
            val activities = dashboardRepo.getRecentActivities().getOrElse { emptyList() }
            val profitability = dashboardRepo.getProjectProfitability().getOrElse { emptyList() }
            val month = java.time.YearMonth.now().toString()
            val salary = dashboardRepo.getSalaryDistribution(month).getOrElse { emptyList() }
            val financials = dashboardRepo.getMonthlyFinancials().getOrElse { emptyList() }
            _state.value = DashboardState(
                isLoading = false,
                summary = summary,
                recentActivities = activities,
                projectProfitability = profitability,
                salaryDistribution = salary,
                monthlyFinancials = financials,
                error = error
            )
        }
    }

    fun refresh() = loadAll()
}
