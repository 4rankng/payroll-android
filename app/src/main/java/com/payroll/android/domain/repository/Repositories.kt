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

interface DashboardRepository {
    suspend fun getSummary(): Result<DashboardSummary>
    suspend fun getFinancialData(period: String): Result<List<FinancialData>>
    suspend fun getRecentActivities(): Result<List<RecentActivity>>
    suspend fun getSalaryDistribution(month: String): Result<List<SalaryDistribution>>
    suspend fun getProjectProfitability(): Result<List<ProjectProfitability>>
    suspend fun getMonthlyFinancials(): Result<List<MonthlyFinancial>>
    suspend fun getPartnerSummary(): Result<PartnerSummary>
}

interface AdminEmployeeRepository {
    suspend fun getSummary(): Result<EmployeeSummary>
    suspend fun getEmployees(page: Int, pageSize: Int, search: String?, status: String?, projectId: Int?): Result<PaginatedResponse<AdminEmployee>>
    suspend fun getEmployee(id: Int): Result<AdminEmployee>
    suspend fun createEmployee(request: CreateEmployeeRequest): Result<AdminEmployee>
    suspend fun updateEmployee(id: Int, request: UpdateEmployeeRequest): Result<AdminEmployee>
    suspend fun deleteEmployee(id: Int): Result<Unit>
    suspend fun getEmployeeProjects(id: Int): Result<List<EmployeeProject>>
    suspend fun getEmployeeTimesheetSummary(id: Int): Result<EmployeeTimesheetSummary>
    suspend fun getEmployeePayroll(id: Int): Result<List<PayrollEntry>>
    suspend fun getMissingBankDetails(): Result<List<BankDetail>>
}

interface ProjectRepository {
    suspend fun getSummary(): Result<ProjectSummary>
    suspend fun getProjects(page: Int, pageSize: Int, search: String?, status: String?, partner: Boolean?): Result<PaginatedResponse<Project>>
    suspend fun getProject(id: Int): Result<Project>
    suspend fun createProject(request: CreateProjectRequest): Result<Project>
    suspend fun updateProject(id: Int, request: UpdateProjectRequest): Result<Project>
    suspend fun updateStatus(id: Int, status: String): Result<Project>
    suspend fun getProjectEmployees(id: Int): Result<List<ProjectEmployee>>
    suspend fun assignEmployee(projectId: Int, request: AssignEmployeeRequest): Result<Unit>
    suspend fun removeEmployee(projectId: Int, request: AssignEmployeeRequest): Result<Unit>
    suspend fun bulkAssign(projectId: Int, request: BulkAssignRequest): Result<Unit>
    suspend fun getProjectFinancial(id: Int): Result<ProjectFinancial>
    suspend fun getActivePayrate(projectId: Int): Result<Payrate>
    suspend fun createPayrate(projectId: Int, request: CreatePayrateRequest): Result<Payrate>
    suspend fun updatePayrate(projectId: Int, payrateId: Int, request: UpdatePayrateRequest): Result<Payrate>
}

interface AdminTimesheetRepository {
    suspend fun getSummary(): Result<AdminTimesheetSummary>
    suspend fun getTimesheets(page: Int, pageSize: Int, status: String?, projectId: Int?, employeeId: Int?, fromDate: String?, toDate: String?): Result<PaginatedResponse<AdminTimesheet>>
    suspend fun getTimesheetDetail(id: Int): Result<AdminTimesheet>
    suspend fun createTimesheet(request: CreateTimesheetRequest): Result<AdminTimesheet>
    suspend fun updateTimesheet(id: Int, request: UpdateTimesheetRequest): Result<AdminTimesheet>
    suspend fun deleteTimesheet(id: Int): Result<Unit>
    suspend fun approveTimesheet(id: Int): Result<Unit>
    suspend fun rejectTimesheet(id: Int, reason: String): Result<Unit>
    suspend fun bulkApprove(ids: List<Int>): Result<Unit>
    suspend fun bulkReject(ids: List<Int>): Result<Unit>
    suspend fun approveAll(): Result<Unit>
    suspend fun getGroupedTimesheets(projectId: Int?): Result<List<GroupedTimesheet>>
}

interface TransactionRepository {
    suspend fun getTransactions(page: Int, pageSize: Int, type: String?, status: String?, fromDate: String?, toDate: String?): Result<PaginatedResponse<Transaction>>
    suspend fun getTransaction(id: Int): Result<Transaction>
    suspend fun createTransaction(request: CreateTransactionRequest): Result<Transaction>
    suspend fun settleTransaction(id: Int, request: SettleRequest): Result<Unit>
    suspend fun reverseTransaction(id: Int): Result<Unit>
    suspend fun getMetadata(): Result<TransactionMetadata>
}

interface LedgerRepository {
    suspend fun getEntries(page: Int, pageSize: Int): Result<PaginatedResponse<LedgerEntry>>
    suspend fun getEntry(id: Int): Result<LedgerEntry>
    suspend fun reverseEntry(id: Int): Result<Unit>
    suspend fun getBalance(): Result<LedgerBalance>
    suspend fun getSummary(): Result<LedgerSummary>
    suspend fun getCashFlow(): Result<List<CashFlowEntry>>
}

interface LoanRepository {
    suspend fun getLenders(page: Int, pageSize: Int): Result<PaginatedResponse<Lender>>
    suspend fun createLender(request: CreateLenderRequest): Result<Lender>
    suspend fun updateLender(id: Int, request: UpdateLenderRequest): Result<Lender>
    suspend fun deleteLender(id: Int): Result<Unit>
    suspend fun getLoans(page: Int, pageSize: Int): Result<PaginatedResponse<Loan>>
    suspend fun createLoan(request: CreateLoanRequest): Result<Loan>
    suspend fun disburseLoan(id: Int, request: DisburseRequest): Result<Unit>
    suspend fun repayLoan(id: Int, request: RepayRequest): Result<Unit>
    suspend fun getLoanSchedule(id: Int): Result<List<RepaymentSchedule>>
}

interface AdminAdvancePaymentRepository {
    suspend fun getSummary(): Result<AdvancePaymentSummary>
    suspend fun getAdvancePayments(page: Int, pageSize: Int, status: String?): Result<PaginatedResponse<AdminAdvancePayment>>
    suspend fun cancelAdvancePayment(id: Int): Result<Unit>
    suspend fun getEligibleEmployees(): Result<List<AdminEmployee>>
    suspend fun getAvailableMonths(): Result<List<String>>
}

interface UserRepository {
    suspend fun getSummary(): Result<UserSummary>
    suspend fun getUsers(page: Int, pageSize: Int, search: String?, role: String?): Result<PaginatedResponse<AdminUser>>
    suspend fun createUser(request: CreateUserRequest): Result<AdminUser>
    suspend fun updateUser(id: Int, request: UpdateUserRequest): Result<AdminUser>
    suspend fun suspendUser(id: Int): Result<Unit>
    suspend fun activateUser(id: Int): Result<Unit>
    suspend fun resetPassword(id: Int): Result<Unit>
}

interface SettingsRepository {
    suspend fun getSettings(): Result<List<Setting>>
    suspend fun updateSetting(id: Int, value: String): Result<Setting>
    suspend fun sendNotification(request: SendNotificationRequest): Result<Unit>
    suspend fun sendEmail(request: SendEmailRequest): Result<Unit>
    suspend fun getEmailHistory(): Result<List<EmailHistoryItem>>
}

interface SystemHealthRepository {
    suspend fun getApiMetrics(): Result<List<ApiMetric>>
    suspend fun getRecentErrors(): Result<List<RecentError>>
    suspend fun getSlowestEndpoints(): Result<List<SlowestEndpoint>>
}
