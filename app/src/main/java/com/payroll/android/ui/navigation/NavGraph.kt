package com.payroll.android.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
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

@Composable
fun PayrollNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Route.Login.route) {
        composable(Route.Login.route) {
            val vm: LoginViewModel = hiltViewModel()
            LoginScreen(
                viewModel = vm,
                onLoginSuccess = {
                    navController.navigate(Route.Router.route) {
                        popUpTo(Route.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Route.Router.route) {
            val vm: EmployeeRouterViewModel = hiltViewModel()
            EmployeeRouterScreen(
                viewModel = vm,
                onNavigateToTimesheet = {
                    navController.navigate(Route.Timesheet.route) {
                        popUpTo(Route.Router.route) { inclusive = true }
                    }
                },
                onNavigateToFlexiblePay = {
                    navController.navigate(Route.FlexiblePay.route) {
                        popUpTo(Route.Router.route) { inclusive = true }
                    }
                },
                onLogout = {
                    navController.navigate(Route.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable(Route.Timesheet.route) {
            val vm: TimesheetViewModel = hiltViewModel()
            TimesheetScreen(
                viewModel = vm,
                onLogout = {
                    navController.navigate(Route.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable(Route.FlexiblePay.route) {
            val vm: FlexiblePayViewModel = hiltViewModel()
            FlexiblePayScreen(
                viewModel = vm,
                onLogout = {
                    navController.navigate(Route.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
