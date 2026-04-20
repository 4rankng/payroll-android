package com.payroll.android.data.remote

import com.payroll.android.data.remote.dto.*
import retrofit2.http.*

interface ApiEndpoints {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("me")
    suspend fun getProfile(): EmployeeProfile

    @PUT("me/password")
    suspend fun changePassword(@Body request: ChangePasswordRequest)

    @GET("me/timesheet")
    suspend fun getTimesheet(
        @Query("fromDate") fromDate: String?,
        @Query("toDate") toDate: String?,
        @Query("sortBy") sortBy: String = "date",
        @Query("sortOrder") sortOrder: String = "desc",
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 50
    ): PaginatedResponse<TimesheetEntry>

    @GET("me/summary")
    suspend fun getSummary(@Query("weeks") weeks: Int = 4): SummaryResponse

    @GET("me/advance-payment")
    suspend fun getAdvancePaymentInfo(): AdvancePaymentInfo

    @POST("me/advance-payment/request")
    suspend fun requestAdvancePayment(@Body request: AdvancePaymentRequest): AdvancePaymentHistoryItem

    @GET("me/advance-payment/history")
    suspend fun getAdvancePaymentHistory(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 10
    ): PaginatedResponse<AdvancePaymentHistoryItem>

    @POST("me/advance-payment/calculate-fee")
    suspend fun calculateFee(@Body request: FeeCalculationRequest): FeeCalculationResponse

    @PUT("me/advance-payment/request/{id}/cancel")
    suspend fun cancelAdvanceRequest(@Path("id") id: Int): AdvancePaymentHistoryItem

    @GET("notifications/unread")
    suspend fun getUnreadCount(): UnreadCountResponse

    @GET("notifications")
    suspend fun getNotifications(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): PaginatedResponse<NotificationItem>

    @PUT("notifications/{id}/read")
    suspend fun markNotificationRead(@Path("id") id: Int)

    @PUT("notifications/read-all")
    suspend fun markAllNotificationsRead()

    @GET("settings/key/{key}")
    suspend fun getSetting(@Path("key") key: String): SettingValue
}
