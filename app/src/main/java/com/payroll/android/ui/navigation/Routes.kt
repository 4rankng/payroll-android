package com.payroll.android.ui.navigation

sealed class Route(val route: String) {
    data object Login : Route("login")
    data object RoleRouter : Route("role_router")

    // Employee
    data object EmployeeHome : Route("employee_home")
    data object Timesheet : Route("timesheet")
    data object FlexiblePay : Route("flexible_pay")

    // Admin tabs
    data object AdminHome : Route("admin_home")
    data object AdminDashboard : Route("admin_dashboard")
    data object AdminEmployees : Route("admin_employees")
    data object AdminEmployeeDetail : Route("admin_employee_detail/{employeeId}") {
        fun create(employeeId: Int) = "admin_employee_detail/$employeeId"
    }
    data object AdminEmployeeForm : Route("admin_employee_form?employeeId={employeeId}") {
        fun create(employeeId: Int? = null) = if (employeeId != null) "admin_employee_form?employeeId=$employeeId" else "admin_employee_form"
    }
    data object AdminProjects : Route("admin_projects")
    data object AdminProjectDetail : Route("admin_project_detail/{projectId}") {
        fun create(projectId: Int) = "admin_project_detail/$projectId"
    }
    data object AdminProjectForm : Route("admin_project_form?projectId={projectId}") {
        fun create(projectId: Int? = null) = if (projectId != null) "admin_project_form?projectId=$projectId" else "admin_project_form"
    }
    data object AdminTimesheets : Route("admin_timesheets")
    data object AdminTimesheetForm : Route("admin_timesheet_form?timesheetId={timesheetId}") {
        fun create(timesheetId: Int? = null) = if (timesheetId != null) "admin_timesheet_form?timesheetId=$timesheetId" else "admin_timesheet_form"
    }
    data object AdminMore : Route("admin_more")
    data object AdminTransactions : Route("admin_transactions")
    data object AdminTransactionDetail : Route("admin_transaction_detail/{transactionId}") {
        fun create(transactionId: Int) = "admin_transaction_detail/$transactionId"
    }
    data object AdminTransactionForm : Route("admin_transaction_form")
    data object AdminLedger : Route("admin_ledger")
    data object AdminLoans : Route("admin_loans")
    data object AdminLoanDetail : Route("admin_loan_detail/{loanId}") {
        fun create(loanId: Int) = "admin_loan_detail/$loanId"
    }
    data object AdminLoanForm : Route("admin_loan_form")
    data object AdminLenderForm : Route("admin_lender_form?lenderId={lenderId}") {
        fun create(lenderId: Int? = null) = if (lenderId != null) "admin_lender_form?lenderId=$lenderId" else "admin_lender_form"
    }
    data object AdminAdvancePayments : Route("admin_advance_payments")
    data object AdminUsers : Route("admin_users")
    data object AdminUserForm : Route("admin_user_form?userId={userId}") {
        fun create(userId: Int? = null) = if (userId != null) "admin_user_form?userId=$userId" else "admin_user_form"
    }
    data object AdminSettings : Route("admin_settings")
    data object AdminSystemHealth : Route("admin_system_health")

    // Partner tabs
    data object PartnerHome : Route("partner_home")
    data object PartnerDashboard : Route("partner_dashboard")
    data object PartnerProjects : Route("partner_projects")
    data object PartnerProjectDetail : Route("partner_project_detail/{projectId}") {
        fun create(projectId: Int) = "partner_project_detail/$projectId"
    }
    data object PartnerTimesheets : Route("partner_timesheets")
    data object PartnerEmployees : Route("partner_employees")
    data object PartnerMore : Route("partner_more")
}
