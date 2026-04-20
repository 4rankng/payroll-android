package com.payroll.android.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AdvancePaymentSummary(
    @SerializedName("total_amount") val totalAmount: Double,
    @SerializedName("pending_count") val pendingCount: Int,
    @SerializedName("approved_count") val approvedCount: Int,
    @SerializedName("completed_count") val completedCount: Int,
    @SerializedName("cancelled_count") val cancelledCount: Int
)

data class AdminAdvancePayment(
    val id: Int,
    @SerializedName("employee_id") val employeeId: Int?,
    @SerializedName("employee_name") val employeeName: String?,
    val amount: Double?,
    @SerializedName("net_amount") val netAmount: Double?,
    val fee: Double?,
    val status: String?,
    @SerializedName("for_month") val forMonth: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)

data class UploadResultRequest(
    val results: List<Map<String, Any>>
)

data class ImportEmployeeListRequest(
    @SerializedName("employee_ids") val employeeIds: List<Int>,
    val month: String
)
