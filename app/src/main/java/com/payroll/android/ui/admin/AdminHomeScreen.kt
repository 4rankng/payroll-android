package com.payroll.android.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.WindowInsets
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.payroll.android.ui.admin.dashboard.DashboardScreen
import com.payroll.android.ui.admin.dashboard.DashboardViewModel
import com.payroll.android.ui.admin.employees.EmployeeListScreen
import com.payroll.android.ui.admin.employees.EmployeeViewModel
import com.payroll.android.ui.admin.employees.EmployeeFormScreen
import com.payroll.android.ui.admin.projects.ProjectListScreen
import com.payroll.android.ui.admin.projects.ProjectDetailScreen
import com.payroll.android.ui.admin.projects.ProjectFormScreen
import com.payroll.android.ui.admin.projects.ProjectViewModel
import com.payroll.android.ui.admin.timesheets.TimesheetListScreen
import com.payroll.android.ui.admin.timesheets.TimesheetFormScreen
import com.payroll.android.ui.admin.timesheets.AdminTimesheetViewModel
import com.payroll.android.ui.admin.more.AdminMoreScreen
import com.payroll.android.ui.admin.transactions.TransactionListScreen
import com.payroll.android.ui.admin.transactions.TransactionFormScreen
import com.payroll.android.ui.admin.transactions.TransactionViewModel
import com.payroll.android.ui.admin.ledger.LedgerScreen
import com.payroll.android.ui.admin.ledger.LedgerViewModel
import com.payroll.android.ui.admin.loans.LoanListScreen
import com.payroll.android.ui.admin.loans.LoanDetailScreen
import com.payroll.android.ui.admin.loans.LoanFormScreen
import com.payroll.android.ui.admin.loans.LoanViewModel
import com.payroll.android.ui.admin.advance.AdvancePaymentListScreen
import com.payroll.android.ui.admin.advance.AdvancePaymentAdminViewModel
import com.payroll.android.ui.admin.users.UserListScreen
import com.payroll.android.ui.admin.users.UserFormScreen
import com.payroll.android.ui.admin.users.UserViewModel
import com.payroll.android.ui.admin.settings.SettingsScreen
import com.payroll.android.ui.admin.settings.SettingsViewModel
import com.payroll.android.ui.admin.systemhealth.SystemHealthScreen
import com.payroll.android.ui.admin.systemhealth.SystemHealthViewModel
import com.payroll.android.ui.components.AdminBottomBar
import com.payroll.android.ui.navigation.Route
import com.payroll.android.ui.theme.*

@Composable
fun AdminHomeScreen(
    onLogout: () -> Unit
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { AdminBottomBar(navController) },
        containerColor = Gray50
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Route.AdminDashboard.route,
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            composable(Route.AdminDashboard.route) {
                val vm: DashboardViewModel = androidx.hilt.navigation.compose.hiltViewModel()
                DashboardScreen(viewModel = vm)
            }
            composable(Route.AdminEmployees.route) {
                val vm: EmployeeViewModel = androidx.hilt.navigation.compose.hiltViewModel()
                EmployeeListScreen(
                    viewModel = vm,
                    onAddEmployee = { navController.navigate(Route.AdminEmployeeForm.create()) },
                    onEditEmployee = { id -> navController.navigate(Route.AdminEmployeeForm.create(id)) }
                )
            }
            composable(Route.AdminEmployeeForm.route) { backStack ->
                val id = backStack.arguments?.getString("employeeId")?.toIntOrNull()
                EmployeeFormScreen(employeeId = id, onBack = { navController.popBackStack() })
            }
            composable(Route.AdminProjects.route) {
                val vm: ProjectViewModel = androidx.hilt.navigation.compose.hiltViewModel()
                ProjectListScreen(
                    viewModel = vm,
                    onAddProject = { navController.navigate(Route.AdminProjectForm.create()) },
                    onProjectClick = { id -> navController.navigate(Route.AdminProjectDetail.create(id)) }
                )
            }
            composable(Route.AdminProjectDetail.route) { backStack ->
                val id = backStack.arguments?.getString("projectId")?.toIntOrNull() ?: return@composable
                val vm: ProjectViewModel = androidx.hilt.navigation.compose.hiltViewModel()
                ProjectDetailScreen(
                    projectId = id, viewModel = vm,
                    onBack = { navController.popBackStack() },
                    onEdit = { pid -> navController.navigate(Route.AdminProjectForm.create(pid)) }
                )
            }
            composable(Route.AdminProjectForm.route) { backStack ->
                val id = backStack.arguments?.getString("projectId")?.toIntOrNull()
                ProjectFormScreen(projectId = id, onBack = { navController.popBackStack() })
            }
            composable(Route.AdminTimesheets.route) {
                val vm: AdminTimesheetViewModel = androidx.hilt.navigation.compose.hiltViewModel()
                TimesheetListScreen(
                    viewModel = vm,
                    onAddTimesheet = { navController.navigate(Route.AdminTimesheetForm.create()) },
                    onEditTimesheet = { id -> navController.navigate(Route.AdminTimesheetForm.create(id)) }
                )
            }
            composable(Route.AdminTimesheetForm.route) { backStack ->
                val id = backStack.arguments?.getString("timesheetId")?.toIntOrNull()
                TimesheetFormScreen(timesheetId = id, onBack = { navController.popBackStack() })
            }
            composable(Route.AdminMore.route) {
                AdminMoreScreen(
                    onNavigate = { route -> navController.navigate(route) },
                    onLogout = onLogout
                )
            }
            composable(Route.AdminTransactions.route) {
                val vm: TransactionViewModel = androidx.hilt.navigation.compose.hiltViewModel()
                TransactionListScreen(
                    viewModel = vm,
                    onAddTransaction = { navController.navigate(Route.AdminTransactionForm.route) },
                    onTransactionClick = { id -> navController.navigate(Route.AdminTransactionDetail.create(id)) }
                )
            }
            composable(Route.AdminTransactionDetail.route) { backStack ->
                val id = backStack.arguments?.getString("transactionId")?.toIntOrNull() ?: return@composable
                val vm: TransactionViewModel = androidx.hilt.navigation.compose.hiltViewModel()
                com.payroll.android.ui.admin.transactions.TransactionDetailScreen(
                    transactionId = id, viewModel = vm, onBack = { navController.popBackStack() }
                )
            }
            composable(Route.AdminTransactionForm.route) {
                TransactionFormScreen(onBack = { navController.popBackStack() })
            }
            composable(Route.AdminLedger.route) {
                val vm: LedgerViewModel = androidx.hilt.navigation.compose.hiltViewModel()
                LedgerScreen(viewModel = vm)
            }
            composable(Route.AdminLoans.route) {
                val vm: LoanViewModel = androidx.hilt.navigation.compose.hiltViewModel()
                LoanListScreen(
                    viewModel = vm,
                    onAddLoan = { navController.navigate(Route.AdminLoanForm.route) },
                    onLoanClick = { id -> navController.navigate(Route.AdminLoanDetail.create(id)) }
                )
            }
            composable(Route.AdminLoanDetail.route) { backStack ->
                val id = backStack.arguments?.getString("loanId")?.toIntOrNull() ?: return@composable
                val vm: LoanViewModel = androidx.hilt.navigation.compose.hiltViewModel()
                LoanDetailScreen(loanId = id, viewModel = vm, onBack = { navController.popBackStack() })
            }
            composable(Route.AdminLoanForm.route) {
                LoanFormScreen(onBack = { navController.popBackStack() })
            }
            composable(Route.AdminAdvancePayments.route) {
                val vm: AdvancePaymentAdminViewModel = androidx.hilt.navigation.compose.hiltViewModel()
                AdvancePaymentListScreen(viewModel = vm)
            }
            composable(Route.AdminUsers.route) {
                val vm: UserViewModel = androidx.hilt.navigation.compose.hiltViewModel()
                UserListScreen(
                    viewModel = vm,
                    onAddUser = { navController.navigate(Route.AdminUserForm.create()) },
                    onEditUser = { id -> navController.navigate(Route.AdminUserForm.create(id)) }
                )
            }
            composable(Route.AdminUserForm.route) { backStack ->
                val id = backStack.arguments?.getString("userId")?.toIntOrNull()
                UserFormScreen(userId = id, onBack = { navController.popBackStack() })
            }
            composable(Route.AdminSettings.route) {
                val vm: SettingsViewModel = androidx.hilt.navigation.compose.hiltViewModel()
                SettingsScreen(viewModel = vm)
            }
            composable(Route.AdminSystemHealth.route) {
                val vm: SystemHealthViewModel = androidx.hilt.navigation.compose.hiltViewModel()
                SystemHealthScreen(viewModel = vm)
            }
        }
    }
}
