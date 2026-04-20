package com.payroll.android.data.remote

import com.payroll.android.data.remote.dto.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.*

interface ApiEndpoints {

    // === Auth ===
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("me")
    suspend fun getProfile(): EmployeeProfile

    @PUT("me/password")
    suspend fun changePassword(@Body request: ChangePasswordRequest)

    // === Employee Portal ===
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

    // === Notifications ===
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

    // === Admin Dashboard ===
    @GET("dashboard/summary")
    suspend fun getDashboardSummary(): DashboardSummary

    @GET("dashboard/financial")
    suspend fun getFinancialData(@Query("period") period: String = "month"): List<FinancialData>

    @GET("dashboard/recent-activities")
    suspend fun getRecentActivities(): List<RecentActivity>

    @GET("dashboard/salary-distribution")
    suspend fun getSalaryDistribution(@Query("month") month: String): List<SalaryDistribution>

    @GET("dashboard/project-profitability")
    suspend fun getProjectProfitability(): List<ProjectProfitability>

    @GET("dashboard/monthly-financials")
    suspend fun getMonthlyFinancials(): List<MonthlyFinancial>

    // === Admin Employees ===
    @GET("employees/summary")
    suspend fun getEmployeeSummary(): EmployeeSummary

    @GET("employees")
    suspend fun getEmployees(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("search") search: String? = null,
        @Query("status") status: String? = null,
        @Query("project_id") projectId: Int? = null
    ): PaginatedResponse<AdminEmployee>

    @GET("employees/{id}")
    suspend fun getEmployee(@Path("id") id: Int): AdminEmployee

    @POST("employees")
    suspend fun createEmployee(@Body request: CreateEmployeeRequest): AdminEmployee

    @PUT("employees/{id}")
    suspend fun updateEmployee(@Path("id") id: Int, @Body request: UpdateEmployeeRequest): AdminEmployee

    @DELETE("employees/{id}")
    suspend fun deleteEmployee(@Path("id") id: Int)

    @GET("employees/{id}/projects")
    suspend fun getEmployeeProjects(@Path("id") id: Int): List<EmployeeProject>

    @GET("employees/{id}/timesheets/summary")
    suspend fun getEmployeeTimesheetSummary(@Path("id") id: Int): EmployeeTimesheetSummary

    @GET("employees/{id}/payroll")
    suspend fun getEmployeePayroll(@Path("id") id: Int): List<PayrollEntry>

    @Multipart
    @POST("employees/import")
    suspend fun importEmployees(@Part file: MultipartBody.Part): List<AdminEmployee>

    @GET("employees/export")
    suspend fun exportEmployees(): ResponseBody

    @GET("employees/missing-bank-details")
    suspend fun getMissingBankDetails(): List<BankDetail>

    // === Admin Projects ===
    @GET("projects/summary")
    suspend fun getProjectSummary(): ProjectSummary

    @GET("projects")
    suspend fun getProjects(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("search") search: String? = null,
        @Query("status") status: String? = null,
        @Query("partner") partner: Boolean? = null
    ): PaginatedResponse<Project>

    @GET("projects/{id}")
    suspend fun getProject(@Path("id") id: Int): Project

    @POST("projects")
    suspend fun createProject(@Body request: CreateProjectRequest): Project

    @PUT("projects/{id}")
    suspend fun updateProject(@Path("id") id: Int, @Body request: UpdateProjectRequest): Project

    @PATCH("projects/{id}/status")
    suspend fun updateProjectStatus(@Path("id") id: Int, @Body request: ProjectStatusRequest): Project

    @GET("projects/{id}/employees")
    suspend fun getProjectEmployees(@Path("id") id: Int): List<ProjectEmployee>

    @POST("projects/{id}/employees")
    suspend fun assignEmployee(@Path("id") id: Int, @Body request: AssignEmployeeRequest)

    @HTTP(method = "DELETE", path = "projects/{id}/employees/remove", hasBody = true)
    suspend fun removeEmployee(@Path("id") id: Int, @Body request: AssignEmployeeRequest)

    @POST("projects/{id}/employees/bulk-assign")
    suspend fun bulkAssign(@Path("id") id: Int, @Body request: BulkAssignRequest)

    @GET("projects/{id}/financial")
    suspend fun getProjectFinancial(@Path("id") id: Int): ProjectFinancial

    @GET("projects/{id}/payrate/active")
    suspend fun getActivePayrate(@Path("id") id: Int): Payrate

    @POST("projects/{id}/payrate")
    suspend fun createPayrate(@Path("id") id: Int, @Body request: CreatePayrateRequest): Payrate

    @PUT("projects/{id}/payrate/{payrateId}")
    suspend fun updatePayrate(@Path("id") id: Int, @Path("payrateId") payrateId: Int, @Body request: UpdatePayrateRequest): Payrate

    @Multipart
    @POST("projects/{id}/timesheets/import")
    suspend fun importProjectTimesheets(@Path("id") id: Int, @Part file: MultipartBody.Part)

    // === Partner Summary ===
    @GET("projects/partner-summary")
    suspend fun getPartnerSummary(): PartnerSummary

    // === Admin Timesheets ===
    @GET("timesheets/summary")
    suspend fun getTimesheetSummary(): AdminTimesheetSummary

    @GET("timesheets")
    suspend fun getAdminTimesheets(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("status") status: String? = null,
        @Query("project_id") projectId: Int? = null,
        @Query("employee_id") employeeId: Int? = null,
        @Query("fromDate") fromDate: String? = null,
        @Query("toDate") toDate: String? = null
    ): PaginatedResponse<AdminTimesheet>

    @GET("timesheets/{id}")
    suspend fun getTimesheetDetail(@Path("id") id: Int): AdminTimesheet

    @POST("timesheets")
    suspend fun createTimesheet(@Body request: CreateTimesheetRequest): AdminTimesheet

    @PUT("timesheets/{id}")
    suspend fun updateTimesheet(@Path("id") id: Int, @Body request: UpdateTimesheetRequest): AdminTimesheet

    @DELETE("timesheets/{id}")
    suspend fun deleteTimesheet(@Path("id") id: Int)

    @POST("timesheets/{id}/approve")
    suspend fun approveTimesheet(@Path("id") id: Int)

    @POST("timesheets/{id}/reject")
    suspend fun rejectTimesheet(@Path("id") id: Int, @Body request: RejectRequest)

    @POST("timesheets/bulk-approve")
    suspend fun bulkApprove(@Body request: BulkActionRequest)

    @POST("timesheets/bulk-reject")
    suspend fun bulkReject(@Body request: BulkActionRequest)

    @POST("timesheets/approve-all")
    suspend fun approveAllTimesheets()

    @GET("timesheets/export")
    suspend fun exportTimesheets(): ResponseBody

    @Multipart
    @POST("timesheets/import")
    suspend fun importTimesheets(@Part file: MultipartBody.Part)

    @GET("timesheets/grouped")
    suspend fun getGroupedTimesheets(
        @Query("project_id") projectId: Int? = null
    ): List<GroupedTimesheet>

    // === Admin Transactions ===
    @GET("transactions")
    suspend fun getTransactions(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("transaction_type") type: String? = null,
        @Query("status") status: String? = null,
        @Query("fromDate") fromDate: String? = null,
        @Query("toDate") toDate: String? = null
    ): PaginatedResponse<Transaction>

    @GET("transactions/{id}")
    suspend fun getTransaction(@Path("id") id: Int): Transaction

    @POST("transactions")
    suspend fun createTransaction(@Body request: CreateTransactionRequest): Transaction

    @POST("transactions/{id}/settle")
    suspend fun settleTransaction(@Path("id") id: Int, @Body request: SettleRequest)

    @POST("transactions/{id}/reverse")
    suspend fun reverseTransaction(@Path("id") id: Int)

    @GET("transactions/metadata")
    suspend fun getTransactionMetadata(): TransactionMetadata

    @GET("transactions/export")
    suspend fun exportTransactions(): ResponseBody

    // === Assets ===
    @Multipart
    @POST("assets/upload")
    suspend fun uploadAsset(@Part file: MultipartBody.Part): AssetUploadResponse

    // === Admin Ledger ===
    @GET("ledger/entries")
    suspend fun getLedgerEntries(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): PaginatedResponse<LedgerEntry>

    @GET("ledger/entries/{id}")
    suspend fun getLedgerEntry(@Path("id") id: Int): LedgerEntry

    @POST("ledger/entries/{id}/reverse")
    suspend fun reverseLedgerEntry(@Path("id") id: Int)

    @GET("ledger/balance")
    suspend fun getLedgerBalance(): LedgerBalance

    @GET("ledger/summary")
    suspend fun getLedgerSummary(): LedgerSummary

    @GET("ledger/accounts/metadata")
    suspend fun getAccountMetadata(): AccountMetadata

    @GET("ledger/cash-flow")
    suspend fun getCashFlow(): List<CashFlowEntry>

    @GET("ledger/export")
    suspend fun exportLedger(): ResponseBody

    // === Admin Loans ===
    @GET("lenders")
    suspend fun getLenders(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): PaginatedResponse<Lender>

    @POST("lenders")
    suspend fun createLender(@Body request: CreateLenderRequest): Lender

    @PUT("lenders/{id}")
    suspend fun updateLender(@Path("id") id: Int, @Body request: UpdateLenderRequest): Lender

    @DELETE("lenders/{id}")
    suspend fun deleteLender(@Path("id") id: Int)

    @GET("loans")
    suspend fun getLoans(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): PaginatedResponse<Loan>

    @POST("loans")
    suspend fun createLoan(@Body request: CreateLoanRequest): Loan

    @POST("loans/{id}/disburse")
    suspend fun disburseLoan(@Path("id") id: Int, @Body request: DisburseRequest)

    @POST("loans/{id}/repay")
    suspend fun repayLoan(@Path("id") id: Int, @Body request: RepayRequest)

    @GET("loans/{id}/schedule")
    suspend fun getLoanSchedule(@Path("id") id: Int): List<RepaymentSchedule>

    // === Admin Advance Payments ===
    @GET("advance-payments/summary")
    suspend fun getAdminAdvanceSummary(): AdvancePaymentSummary

    @GET("advance-payments")
    suspend fun getAdminAdvancePayments(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("status") status: String? = null
    ): PaginatedResponse<AdminAdvancePayment>

    @Multipart
    @POST("advance-payments/import")
    suspend fun importAdvancePayments(@Part file: MultipartBody.Part)

    @POST("advance-payments/upload-result")
    suspend fun uploadAdvanceResult(@Body request: UploadResultRequest)

    @GET("advance-payments/export")
    suspend fun exportAdvancePayments(): ResponseBody

    @POST("advance-payments/{id}/cancel")
    suspend fun cancelAdminAdvancePayment(@Path("id") id: Int)

    @GET("advance-payments/employees")
    suspend fun getEligibleEmployees(): List<AdminEmployee>

    @GET("advance-payments/available-months")
    suspend fun getAvailableMonths(): List<String>

    @POST("advance-payments/import-employee-list")
    suspend fun importEmployeeList(@Body request: ImportEmployeeListRequest)

    // === Admin Users ===
    @GET("users/summary")
    suspend fun getUserSummary(): UserSummary

    @GET("users")
    suspend fun getUsers(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("search") search: String? = null,
        @Query("role") role: String? = null
    ): PaginatedResponse<AdminUser>

    @POST("users")
    suspend fun createUser(@Body request: CreateUserRequest): AdminUser

    @PUT("users/{id}")
    suspend fun updateUser(@Path("id") id: Int, @Body request: UpdateUserRequest): AdminUser

    @POST("users/{id}/suspend")
    suspend fun suspendUser(@Path("id") id: Int)

    @POST("users/{id}/activate")
    suspend fun activateUser(@Path("id") id: Int)

    @POST("users/{id}/reset-password")
    suspend fun resetUserPassword(@Path("id") id: Int)

    // === Admin Settings ===
    @GET("settings")
    suspend fun getSettings(): List<Setting>

    @PUT("settings/{id}")
    suspend fun updateSetting(@Path("id") id: Int, @Body request: UpdateSettingRequest): Setting

    @POST("notification")
    suspend fun sendNotification(@Body request: SendNotificationRequest)

    @POST("email/send")
    suspend fun sendEmail(@Body request: SendEmailRequest)

    @GET("email/history")
    suspend fun getEmailHistory(): List<EmailHistoryItem>

    // === System Health ===
    @GET("metrics/api")
    suspend fun getApiMetrics(): List<ApiMetric>

    @GET("metrics/errors/recent")
    suspend fun getRecentErrors(): List<RecentError>

    @GET("metrics/latency/slowest")
    suspend fun getSlowestEndpoints(): List<SlowestEndpoint>
}
