package com.payroll.android.ui.role

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.payroll.android.ui.theme.Sky500

@Composable
fun RoleRouterScreen(
    viewModel: RoleRouterViewModel,
    onNavigateToAdmin: () -> Unit,
    onNavigateToPartner: () -> Unit,
    onNavigateToEmployee: () -> Unit,
    onLogout: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.role) {
        when (state.role) {
            "admin" -> onNavigateToAdmin()
            "partner" -> onNavigateToPartner()
            "employee" -> onNavigateToEmployee()
        }
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
