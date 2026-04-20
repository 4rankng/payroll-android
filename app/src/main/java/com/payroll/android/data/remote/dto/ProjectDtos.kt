package com.payroll.android.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ProjectSummary(
    @SerializedName("total_projects") val totalProjects: Int,
    @SerializedName("active_projects") val activeProjects: Int,
    @SerializedName("total_revenue") val totalRevenue: Double,
    @SerializedName("total_expenses") val totalExpenses: Double
)

data class Project(
    val id: Int,
    val name: String?,
    val code: String?,
    @SerializedName("client_name") val clientName: String?,
    val description: String?,
    @SerializedName("start_date") val startDate: String?,
    @SerializedName("end_date") val endDate: String?,
    val status: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("employee_count") val employeeCount: Int? = null,
    @SerializedName("total_hours") val totalHours: Double? = null,
    @SerializedName("pending_timesheets") val pendingTimesheets: Int? = null,
    @SerializedName("approved_timesheets") val approvedTimesheets: Int? = null
)

data class CreateProjectRequest(
    val name: String,
    val code: String? = null,
    @SerializedName("client_name") val clientName: String? = null,
    val description: String? = null,
    @SerializedName("start_date") val startDate: String? = null,
    @SerializedName("end_date") val endDate: String? = null,
    val status: String? = "active"
)

data class UpdateProjectRequest(
    val name: String? = null,
    val code: String? = null,
    @SerializedName("client_name") val clientName: String? = null,
    val description: String? = null,
    @SerializedName("start_date") val startDate: String? = null,
    @SerializedName("end_date") val endDate: String? = null,
    val status: String? = null
)

data class ProjectEmployee(
    val id: Int,
    val fullname: String?,
    val status: String?,
    val role: String?
)

data class ProjectFinancial(
    val revenue: Double?,
    val expenses: Double?,
    val profit: Double?,
    @SerializedName("total_hours") val totalHours: Double?
)

data class Payrate(
    val id: Int,
    @SerializedName("employee_id") val employeeId: Int?,
    @SerializedName("hourly_rate") val hourlyRate: Double?,
    @SerializedName("overtime_rate") val overtimeRate: Double?,
    @SerializedName("effective_date") val effectiveDate: String?,
    val status: String?
)

data class CreatePayrateRequest(
    @SerializedName("employee_id") val employeeId: Int,
    @SerializedName("hourly_rate") val hourlyRate: Double,
    @SerializedName("overtime_rate") val overtimeRate: Double? = null,
    @SerializedName("effective_date") val effectiveDate: String? = null
)

data class UpdatePayrateRequest(
    @SerializedName("hourly_rate") val hourlyRate: Double? = null,
    @SerializedName("overtime_rate") val overtimeRate: Double? = null,
    @SerializedName("effective_date") val effectiveDate: String? = null
)

data class AssignEmployeeRequest(
    @SerializedName("employee_id") val employeeId: Int,
    val role: String? = null
)

data class BulkAssignRequest(
    @SerializedName("employee_ids") val employeeIds: List<Int>,
    val role: String? = null
)

data class ProjectStatusRequest(
    val status: String
)
