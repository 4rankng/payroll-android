package com.payroll.android.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.payroll.android.ui.navigation.Route
import com.payroll.android.ui.theme.*

sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    data object Dashboard : BottomNavItem(Route.AdminDashboard.route, "Tổng quan", Icons.Default.Dashboard)
    data object Employees : BottomNavItem(Route.AdminEmployees.route, "Nhân viên", Icons.Default.People)
    data object Projects : BottomNavItem(Route.AdminProjects.route, "Dự án", Icons.Default.Folder)
    data object Timesheets : BottomNavItem(Route.AdminTimesheets.route, "Chấm công", Icons.Default.Schedule)
    data object More : BottomNavItem(Route.AdminMore.route, "Thêm", Icons.Default.MoreHoriz)
}

sealed class PartnerNavItem(val route: String, val label: String, val icon: ImageVector) {
    data object Dashboard : PartnerNavItem(Route.PartnerDashboard.route, "Tổng quan", Icons.Default.Dashboard)
    data object Projects : PartnerNavItem(Route.PartnerProjects.route, "Dự án", Icons.Default.Folder)
    data object Timesheets : PartnerNavItem(Route.PartnerTimesheets.route, "Chấm công", Icons.Default.Schedule)
    data object More : PartnerNavItem(Route.PartnerMore.route, "Thêm", Icons.Default.MoreHoriz)
}

val AdminBottomItems = listOf(
    BottomNavItem.Dashboard,
    BottomNavItem.Employees,
    BottomNavItem.Projects,
    BottomNavItem.Timesheets,
    BottomNavItem.More
)

val PartnerBottomItems = listOf(
    PartnerNavItem.Dashboard,
    PartnerNavItem.Projects,
    PartnerNavItem.Timesheets,
    PartnerNavItem.More
)

@Composable
fun AdminBottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val tabRoutes = AdminBottomItems.map { it.route }
    if (currentRoute !in tabRoutes) return

    NavigationBar(
        containerColor = White,
        tonalElevation = 8.dp
    ) {
        AdminBottomItems.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) navController.navigate(item.route) {
                        popUpTo(Route.AdminHome.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(item.icon, item.label, tint = if (selected) Sky500 else Gray400)
                },
                label = {
                    Text(item.label, color = if (selected) Sky500 else Gray400, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal, style = MaterialTheme.typography.labelSmall)
                }
            )
        }
    }
}

@Composable
fun PartnerBottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val tabRoutes = PartnerBottomItems.map { it.route }
    if (currentRoute !in tabRoutes) return

    NavigationBar(
        containerColor = White,
        tonalElevation = 8.dp
    ) {
        PartnerBottomItems.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) navController.navigate(item.route) {
                        popUpTo(Route.PartnerHome.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(item.icon, item.label, tint = if (selected) Sky500 else Gray400)
                },
                label = {
                    Text(item.label, color = if (selected) Sky500 else Gray400, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal, style = MaterialTheme.typography.labelSmall)
                }
            )
        }
    }
}
