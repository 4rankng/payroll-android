package com.payroll.android.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AdminUser(
    val id: Int,
    val username: String?,
    val email: String?,
    val fullname: String?,
    val role: String?,
    val status: String?,
    @SerializedName("last_login") val lastLogin: String?,
    @SerializedName("created_at") val createdAt: String?
)

data class UserSummary(
    @SerializedName("total_users") val totalUsers: Int,
    @SerializedName("active_users") val activeUsers: Int,
    @SerializedName("suspended_users") val suspendedUsers: Int
)

data class CreateUserRequest(
    val username: String,
    val email: String? = null,
    val fullname: String? = null,
    val password: String,
    val role: String
)

data class UpdateUserRequest(
    val username: String? = null,
    val email: String? = null,
    val fullname: String? = null,
    val password: String? = null,
    val role: String? = null
)
