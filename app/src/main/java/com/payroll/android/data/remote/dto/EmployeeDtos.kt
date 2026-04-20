package com.payroll.android.data.remote.dto

import com.google.gson.annotations.SerializedName

data class EmployeeSummary(
    @SerializedName("total_employees") val totalEmployees: Int,
    @SerializedName("total_working_employees") val totalWorkingEmployees: Int,
    @SerializedName("employees_hired_this_month") val employeesHiredThisMonth: Int,
    @SerializedName("salary_month_to_date") val salaryMonthToDate: Double,
    @SerializedName("paid_month_to_date") val paidMonthToDate: Double
)

data class AdminEmployee(
    val id: Int,
    val fullname: String?,
    val email: String?,
    val username: String?,
    val mobile: String?,
    val cccd: String?,
    val address: String?,
    @SerializedName("date_of_birth") val dateOfBirth: String?,
    val bank: String?,
    @SerializedName("bank_account_number") val bankAccountNumber: String?,
    @SerializedName("bank_account_name") val bankAccountName: String?,
    val status: String?,
    @SerializedName("base_salary") val baseSalary: Double?,
    @SerializedName("hourly_rate") val hourlyRate: Double?,
    @SerializedName("avatar_url") val avatarUrl: String?,
    @SerializedName("created_at") val createdAt: String?,
    val role: String?
)

data class CreateEmployeeRequest(
    val fullname: String,
    val email: String? = null,
    val cccd: String? = null,
    val address: String? = null,
    val mobile: String? = null,
    val bank: String? = null,
    @SerializedName("bank_account_number") val bankAccountNumber: String? = null,
    @SerializedName("bank_account_name") val bankAccountName: String? = null,
    @SerializedName("date_of_birth") val dateOfBirth: String? = null
)

data class UpdateEmployeeRequest(
    val fullname: String? = null,
    val email: String? = null,
    val cccd: String? = null,
    val address: String? = null,
    val mobile: String? = null,
    val bank: String? = null,
    @SerializedName("bank_account_number") val bankAccountNumber: String? = null,
    @SerializedName("bank_account_name") val bankAccountName: String? = null,
    @SerializedName("date_of_birth") val dateOfBirth: String? = null
)

data class EmployeeProject(
    val id: Int,
    val name: String?,
    val code: String?,
    val status: String?,
    val role: String?
)

data class EmployeeTimesheetSummary(
    @SerializedName("total_hours") val totalHours: Double,
    @SerializedName("total_days") val totalDays: Int,
    @SerializedName("total_salary") val totalSalary: Double
)

data class PayrollEntry(
    val id: Int,
    val month: String?,
    val amount: Double?,
    val status: String?,
    @SerializedName("paid_at") val paidAt: String?
)
