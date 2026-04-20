package com.payroll.android.data.remote.dto

import com.google.gson.annotations.SerializedName

data class DashboardSummary(
    @SerializedName("total_employees") val totalEmployees: Int,
    @SerializedName("active_employees") val activeEmployees: Int,
    @SerializedName("total_projects") val totalProjects: Int,
    @SerializedName("monthly_revenue") val monthlyRevenue: Double,
    @SerializedName("monthly_expenses") val monthlyExpenses: Double,
    @SerializedName("net_profit") val netProfit: Double,
    @SerializedName("total_pending_timesheets") val totalPendingTimesheets: Int,
    @SerializedName("total_approved_timesheets") val totalApprovedTimesheets: Int,
    @SerializedName("pending_approvals") val pendingApprovals: Int
)

data class FinancialData(
    val period: String?,
    val revenue: Double?,
    val expenses: Double?,
    val profit: Double?
)

data class RecentActivity(
    val id: Int?,
    val action: String?,
    val user: String?,
    val target: String?,
    @SerializedName("created_at") val createdAt: String?
)

data class SalaryDistribution(
    val employee: String?,
    val amount: Double?,
    val project: String?
)

data class ProjectProfitability(
    val project: String?,
    val revenue: Double?,
    val expenses: Double?,
    val profit: Double?
)

data class MonthlyFinancial(
    val month: String?,
    val revenue: Double?,
    val expenses: Double?,
    val profit: Double?
)

data class PartnerSummary(
    @SerializedName("total_projects") val totalProjects: Int,
    @SerializedName("active_projects") val activeProjects: Int,
    @SerializedName("total_employees") val totalEmployees: Int,
    @SerializedName("total_hours") val totalHours: Double
)
