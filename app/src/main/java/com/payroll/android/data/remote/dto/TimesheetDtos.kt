package com.payroll.android.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AdminTimesheetSummary(
    @SerializedName("total_timesheets") val totalTimesheets: Int,
    val pending: Int,
    val approved: Int,
    val rejected: Int,
    @SerializedName("total_hours") val totalHours: Double
)

data class AdminTimesheet(
    val id: Int,
    @SerializedName("employee_id") val employeeId: Int?,
    @SerializedName("employee_name") val employeeName: String?,
    @SerializedName("project_id") val projectId: Int?,
    @SerializedName("project_name") val projectName: String?,
    val date: String?,
    @SerializedName("hours_worked") val hoursWorked: Double?,
    val paytype: String?,
    val payrate: Double?,
    @SerializedName("total_salary") val totalSalary: Double?,
    val status: String?,
    val note: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)

data class CreateTimesheetRequest(
    @SerializedName("employee_id") val employeeId: Int,
    @SerializedName("project_id") val projectId: Int,
    val date: String,
    @SerializedName("hours_worked") val hoursWorked: Double,
    val paytype: String? = null,
    val payrate: Double? = null,
    val note: String? = null
)

data class UpdateTimesheetRequest(
    @SerializedName("employee_id") val employeeId: Int? = null,
    @SerializedName("project_id") val projectId: Int? = null,
    val date: String? = null,
    @SerializedName("hours_worked") val hoursWorked: Double? = null,
    val paytype: String? = null,
    val payrate: Double? = null,
    val note: String? = null
)

data class RejectRequest(
    val reason: String
)

data class BulkActionRequest(
    val ids: List<Int>
)

data class GroupedTimesheet(
    @SerializedName("employee_id") val employeeId: Int?,
    @SerializedName("employee_name") val employeeName: String?,
    val entries: List<AdminTimesheet>?
)
