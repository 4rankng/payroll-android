package com.payroll.android.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.payroll.android.ui.login.LoginScreen
import com.payroll.android.ui.login.LoginViewModel
import com.payroll.android.ui.home.EmployeeRouterScreen
import com.payroll.android.ui.home.EmployeeRouterViewModel
import com.payroll.android.ui.timesheet.TimesheetScreen
import com.payroll.android.ui.timesheet.TimesheetViewModel
import com.payroll.android.ui.flexiblepay.FlexiblePayScreen
import com.payroll.android.ui.flexiblepay.FlexiblePayViewModel
import com.payroll.android.ui.role.RoleRouterScreen
import com.payroll.android.ui.role.RoleRouterViewModel
import com.payroll.android.ui.admin.AdminHomeScreen
import com.payroll.android.ui.partner.PartnerHomeScreen

@Composable
fun PayrollNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Route.Login.route) {
        // Login
        composable(Route.Login.route) {
            val vm: LoginViewModel = androidx.hilt.navigation.compose.hiltViewModel()
            LoginScreen(
                viewModel = vm,
                onLoginSuccess = {
                    navController.navigate(Route.RoleRouter.route) {
                        popUpTo(Route.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Role Router - determines admin/partner/employee
        composable(Route.RoleRouter.route) {
            val vm: RoleRouterViewModel = androidx.hilt.navigation.compose.hiltViewModel()
            RoleRouterScreen(
                viewModel = vm,
                onNavigateToAdmin = {
                    navController.navigate(Route.AdminHome.route) {
                        popUpTo(Route.RoleRouter.route) { inclusive = true }
                    }
                },
                onNavigateToPartner = {
                    navController.navigate(Route.PartnerHome.route) {
                        popUpTo(Route.RoleRouter.route) { inclusive = true }
                    }
                },
                onNavigateToEmployee = {
                    navController.navigate(Route.EmployeeHome.route) {
                        popUpTo(Route.RoleRouter.route) { inclusive = true }
                    }
                },
                onLogout = {
                    navController.navigate(Route.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Admin Home (contains its own nav with bottom bar)
        composable(Route.AdminHome.route) {
            AdminHomeScreen(
                onLogout = {
                    navController.navigate(Route.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Partner Home (contains its own nav with bottom bar)
        composable(Route.PartnerHome.route) {
            PartnerHomeScreen(
                onLogout = {
                    navController.navigate(Route.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Employee Home (existing flow)
        composable(Route.EmployeeHome.route) {
            val vm: EmployeeRouterViewModel = androidx.hilt.navigation.compose.hiltViewModel()
            EmployeeRouterScreen(
                viewModel = vm,
                onNavigateToTimesheet = {
                    navController.navigate(Route.Timesheet.route) {
                        popUpTo(Route.EmployeeHome.route) { inclusive = true }
                    }
                },
                onNavigateToFlexiblePay = {
                    navController.navigate(Route.FlexiblePay.route) {
                        popUpTo(Route.EmployeeHome.route) { inclusive = true }
                    }
                },
                onLogout = {
                    navController.navigate(Route.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Employee Timesheet
        composable(Route.Timesheet.route) {
            val vm: TimesheetViewModel = androidx.hilt.navigation.compose.hiltViewModel()
            TimesheetScreen(
                viewModel = vm,
                onLogout = {
                    navController.navigate(Route.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Employee Flexible Pay
        composable(Route.FlexiblePay.route) {
            val vm: FlexiblePayViewModel = androidx.hilt.navigation.compose.hiltViewModel()
            FlexiblePayScreen(
                onLogout = {
                    navController.navigate(Route.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
