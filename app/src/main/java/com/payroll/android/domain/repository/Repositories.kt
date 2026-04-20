package com.payroll.android.domain.repository

import com.payroll.android.data.remote.dto.*

interface AuthRepository {
    suspend fun login(username: String, password: String): Result<LoginResponse>
    suspend fun logout()
    suspend fun isLoggedIn(): Boolean
}

interface EmployeeRepository {
    suspend fun getProfile(): Result<EmployeeProfile>
    suspend fun changePassword(current: String, new: String): Result<Unit>
    suspend fun getTimesheet(fromDate: String?, toDate: String?, page: Int, pageSize: Int): Result<PaginatedResponse<TimesheetEntry>>
    suspend fun getSummary(weeks: Int): Result<SummaryResponse>
    suspend fun getSetting(key: String): Result<String?>
}

interface AdvancePaymentRepository {
    suspend fun getInfo(): Result<AdvancePaymentInfo>
    suspend fun requestAdvance(amount: Double): Result<AdvancePaymentHistoryItem>
    suspend fun getHistory(page: Int, pageSize: Int): Result<PaginatedResponse<AdvancePaymentHistoryItem>>
    suspend fun calculateFee(amount: Double): Result<FeeCalculationResponse>
    suspend fun cancelRequest(id: Int): Result<AdvancePaymentHistoryItem>
}

interface NotificationRepository {
    suspend fun getUnreadCount(): Result<Int>
    suspend fun getNotifications(page: Int, pageSize: Int): Result<PaginatedResponse<NotificationItem>>
    suspend fun markRead(id: Int): Result<Unit>
    suspend fun markAllRead(): Result<Unit>
}
