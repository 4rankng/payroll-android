package com.payroll.android.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val user: UserDto,
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("expires_in") val expiresIn: Long
)

data class UserDto(
    val id: Int,
    val username: String,
    val fullname: String?,
    val email: String?
)

data class EmployeeProfile(
    val id: Int,
    val fullname: String?,
    val email: String?,
    val username: String,
    val mobile: String?,
    @SerializedName("payment_schedule") val paymentSchedule: String?,
    val bank: String?,
    @SerializedName("bank_account") val bankAccount: String?,
    @SerializedName("base_salary") val baseSalary: Double?,
    @SerializedName("hourly_rate") val hourlyRate: Double?,
    @SerializedName("avatar_url") val avatarUrl: String?
)

data class TimesheetEntry(
    val id: Int,
    val date: String,
    @SerializedName("check_in") val checkIn: String?,
    @SerializedName("check_out") val checkOut: String?,
    @SerializedName("hours_worked") val hoursWorked: Double?,
    @SerializedName("total_salary") val totalSalary: Double?,
    @SerializedName("amount_received") val amountReceived: Double?,
    @SerializedName("payment_status") val paymentStatus: String?,
    val note: String?
)

data class PaginatedResponse<T>(
    val items: List<T>,
    val page: Int,
    @SerializedName("pageSize") val pageSize: Int,
    val total: Int,
    @SerializedName("totalPages") val totalPages: Int
)

data class SummaryResponse(
    @SerializedName("total_salary") val totalSalary: Double,
    @SerializedName("total_hours") val totalHours: Double,
    @SerializedName("total_days") val totalDays: Int,
    @SerializedName("amount_received") val amountReceived: Double,
    @SerializedName("payable_limit") val payableLimit: Double?,
    val month: String?
)

data class AdvancePaymentInfo(
    @SerializedName("forMonth") val forMonth: String?,
    @SerializedName("maxAdvanceAmount") val maxAdvanceAmount: Double,
    @SerializedName("completedAmount") val completedAmount: Double,
    @SerializedName("pendingAmount") val pendingAmount: Double,
    @SerializedName("remainingAmount") val remainingAmount: Double,
    @SerializedName("canRequest") val canRequest: Boolean,
    @SerializedName("feePercentage") val feePercentage: Double,
    @SerializedName("minFee") val minFee: Double
)

data class AdvancePaymentRequest(
    val amount: Double
)

data class AdvancePaymentHistoryItem(
    val id: Int,
    val amount: Double,
    val status: String,
    @SerializedName("net_amount") val netAmount: Double?,
    val fee: Double?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)

data class FeeCalculationRequest(
    val amount: Double
)

data class FeeCalculationResponse(
    val fee: Double,
    @SerializedName("netAmount") val netAmount: Double
)

data class NotificationItem(
    val id: Int,
    val title: String?,
    val message: String?,
    val read: Boolean?,
    @SerializedName("created_at") val createdAt: String?,
    val type: String?
)

data class UnreadCountResponse(
    val count: Int
)

data class ChangePasswordRequest(
    @SerializedName("current_password") val currentPassword: String,
    @SerializedName("new_password") val newPassword: String
)

data class SettingValue(
    val key: String,
    val value: String?
)

// Generic API response wrapper
data class ApiResponse<T>(
    val data: T?,
    val message: String?,
    val success: Boolean?
)
