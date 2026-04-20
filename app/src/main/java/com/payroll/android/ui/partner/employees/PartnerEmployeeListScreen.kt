package com.payroll.android.ui.partner.employees

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.payroll.android.ui.components.*
import com.payroll.android.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartnerEmployeeListScreen(
    viewModel: PartnerEmployeeViewModel
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) { viewModel.loadAll() }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = { TopAppBar(title = { Text("Nhân viên", fontWeight = FontWeight.Bold) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = White)) },
        containerColor = Gray50
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            // Project filter
            if (state.projects.isNotEmpty()) {
                Row(Modifier.padding(horizontal = 16.dp, vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip("Tất cả", state.selectedProjectId == null) { viewModel.onProjectFilter(null) }
                    state.projects.take(5).forEach { p ->
                        FilterChip(p.name ?: "", state.selectedProjectId == p.id) { viewModel.onProjectFilter(p.id) }
                    }
                }
            }

            SearchBar(state.search, viewModel::onSearch, "Tìm nhân viên...", Modifier.padding(horizontal = 16.dp, vertical = 4.dp))

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Sky500) }
            } else if (state.employees.isEmpty()) {
                EmptyState(Icons.Default.People, "Không có nhân viên")
            } else {
                LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.employees, key = { it.id }) { emp ->
                        GlassCard {
                            // NO salary, NO payrate, NO bank details
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(Modifier.weight(1f)) {
                                    Text(emp.fullname ?: "N/A", fontWeight = FontWeight.SemiBold, color = Gray800)
                                    emp.email?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = Gray500) }
                                    emp.mobile?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = Gray500) }
                                }
                                StatusBadge(emp.status ?: "active")
                            }
                            emp.role?.let {
                                Surface(shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp), color = Sky100) {
                                    Text(it, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = Sky700)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
