package com.payroll.android.data.remote.dto

import com.google.gson.annotations.SerializedName

data class Setting(
    val id: Int,
    val key: String?,
    val value: String?,
    val description: String?
)

data class UpdateSettingRequest(
    val value: String
)

data class SendNotificationRequest(
    @SerializedName("user_id") val userId: Int? = null,
    val title: String,
    val message: String
)

data class SendEmailRequest(
    val recipients: List<String>,
    val subject: String,
    val body: String,
    val attachments: List<String>? = null
)

data class EmailHistoryItem(
    val id: Int,
    val recipients: List<String>?,
    val subject: String?,
    val status: String?,
    @SerializedName("sent_at") val sentAt: String?
)

data class ApiMetric(
    val endpoint: String?,
    @SerializedName("total_requests") val totalRequests: Int?,
    @SerializedName("avg_latency_ms") val avgLatencyMs: Double?,
    @SerializedName("error_rate") val errorRate: Double?
)

data class RecentError(
    val id: Int?,
    val endpoint: String?,
    val method: String?,
    @SerializedName("status_code") val statusCode: Int?,
    val message: String?,
    @SerializedName("created_at") val createdAt: String?
)

data class SlowestEndpoint(
    val endpoint: String?,
    @SerializedName("avg_latency_ms") val avgLatencyMs: Double?,
    @SerializedName("max_latency_ms") val maxLatencyMs: Double?,
    @SerializedName("request_count") val requestCount: Int?
)
