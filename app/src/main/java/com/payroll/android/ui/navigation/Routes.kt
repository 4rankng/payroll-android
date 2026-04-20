package com.payroll.android.ui.navigation

sealed class Route(val route: String) {
    data object Login : Route("login")
    data object Router : Route("router")
    data object Timesheet : Route("timesheet")
    data object FlexiblePay : Route("flexible_pay")
}
