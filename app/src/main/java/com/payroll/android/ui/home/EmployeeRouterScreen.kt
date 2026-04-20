package com.payroll.android.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.payroll.android.ui.theme.Sky500

@Composable
fun EmployeeRouterScreen(
    viewModel: EmployeeRouterViewModel,
    onNavigateToTimesheet: () -> Unit,
    onNavigateToFlexiblePay: () -> Unit,
    onLogout: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.profile) {
        val profile = state.profile ?: return@LaunchedEffect
        when (profile.paymentSchedule) {
            "flexible" -> onNavigateToFlexiblePay()
            else -> onNavigateToTimesheet() // weekly, monthly, or null
        }
    }

    LaunchedEffect(state.isLoggedOut) {
        if (state.isLoggedOut) onLogout()
    }

    LaunchedEffect(state.error) {
        if (state.error != null) onLogout()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(color = Sky500)
        }
    }
}
