package com.payroll.android.ui.partner

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.payroll.android.ui.partner.dashboard.PartnerDashboardScreen
import com.payroll.android.ui.partner.dashboard.PartnerDashboardViewModel
import com.payroll.android.ui.partner.projects.PartnerProjectListScreen
import com.payroll.android.ui.partner.projects.PartnerProjectDetailScreen
import com.payroll.android.ui.partner.projects.PartnerProjectViewModel
import com.payroll.android.ui.partner.timesheets.PartnerTimesheetScreen
import com.payroll.android.ui.partner.timesheets.PartnerTimesheetViewModel
import com.payroll.android.ui.partner.employees.PartnerEmployeeListScreen
import com.payroll.android.ui.partner.employees.PartnerEmployeeViewModel
import com.payroll.android.ui.admin.more.PartnerMoreScreen
import com.payroll.android.ui.components.PartnerBottomBar
import com.payroll.android.ui.navigation.Route

@Composable
fun PartnerHomeScreen(
    onLogout: () -> Unit
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { PartnerBottomBar(navController) },
        containerColor = androidx.compose.ui.graphics.Color.Transparent
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Route.PartnerDashboard.route,
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            composable(Route.PartnerDashboard.route) {
                val vm: PartnerDashboardViewModel = androidx.hilt.navigation.compose.hiltViewModel()
                PartnerDashboardScreen(viewModel = vm, onProjectClick = { id -> navController.navigate(Route.PartnerProjectDetail.create(id)) })
            }
            composable(Route.PartnerProjects.route) {
                val vm: PartnerProjectViewModel = androidx.hilt.navigation.compose.hiltViewModel()
                PartnerProjectListScreen(viewModel = vm, onProjectClick = { id -> navController.navigate(Route.PartnerProjectDetail.create(id)) })
            }
            composable(Route.PartnerProjectDetail.route) { backStack ->
                val id = backStack.arguments?.getString("projectId")?.toIntOrNull() ?: return@composable
                val vm: PartnerProjectViewModel = androidx.hilt.navigation.compose.hiltViewModel()
                PartnerProjectDetailScreen(projectId = id, viewModel = vm, onBack = { navController.popBackStack() })
            }
            composable(Route.PartnerTimesheets.route) {
                val vm: PartnerTimesheetViewModel = androidx.hilt.navigation.compose.hiltViewModel()
                PartnerTimesheetScreen(viewModel = vm)
            }
            composable(Route.PartnerMore.route) {
                PartnerMoreScreen(
                    onNavigate = { route ->
                        when (route) {
                            "partner_employees" -> navController.navigate(Route.PartnerEmployees.route)
                            "admin_settings" -> navController.navigate(Route.AdminSettings.route)
                        }
                    },
                    onLogout = onLogout
                )
            }
            composable(Route.PartnerEmployees.route) {
                val vm: PartnerEmployeeViewModel = androidx.hilt.navigation.compose.hiltViewModel()
                PartnerEmployeeListScreen(viewModel = vm)
            }
        }
    }
}
