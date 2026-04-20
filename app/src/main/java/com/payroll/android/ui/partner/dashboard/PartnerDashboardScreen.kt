package com.payroll.android.ui.partner.dashboard

import androidx.compose.foundation.layout.*
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
fun PartnerDashboardScreen(
    viewModel: PartnerDashboardViewModel,
    onProjectClick: (Int) -> Unit
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) { viewModel.loadAll() }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Tổng quan", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White),
                actions = { IconButton(onClick = { viewModel.loadAll() }) { Icon(Icons.Default.Refresh, null, tint = Sky500) } })
        }, containerColor = Gray50
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Sky500) }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                state.summary?.let { s ->
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatCard("Dự án", "${s.totalProjects}", Modifier.weight(1f))
                            StatCard("Đang hoạt động", "${s.activeProjects}", Modifier.weight(1f), color = Emerald500)
                        }
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatCard("Nhân viên", "${s.totalEmployees}", Modifier.weight(1f))
                            StatCard("Tổng giờ", "${s.totalHours}h", Modifier.weight(1f))
                        }
                    }
                }

                // Timesheet status - NO amounts
                item { SectionHeader("Trạng thái chấm công") }
                item {
                    GlassCard {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${state.timesheetStatus.first}", fontWeight = FontWeight.Bold, color = Amber500)
                                Text("Chờ duyệt", style = MaterialTheme.typography.labelSmall, color = Gray500)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${state.timesheetStatus.second}", fontWeight = FontWeight.Bold, color = Emerald500)
                                Text("Đã duyệt", style = MaterialTheme.typography.labelSmall, color = Gray500)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${state.timesheetStatus.third}", fontWeight = FontWeight.Bold, color = Red500)
                                Text("Từ chối", style = MaterialTheme.typography.labelSmall, color = Gray500)
                            }
                        }
                    }
                }

                // Projects - NO revenue/expenses
                item { SectionHeader("Dự án của bạn") }
                items(state.projects) { project ->
                    GlassCard {
                        Column {
                            Text(project.name ?: "", fontWeight = FontWeight.SemiBold, color = Gray800)
                            project.code?.let { Text("Mã: $it", style = MaterialTheme.typography.bodySmall, color = Gray500) }
                            Spacer(Modifier.height(4.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("${project.employeeCount ?: 0} nhân viên", style = MaterialTheme.typography.bodySmall, color = Sky600)
                                StatusBadge(project.status ?: "active")
                            }
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Text("Chờ duyệt: ${project.pendingTimesheets ?: 0}", style = MaterialTheme.typography.labelSmall, color = Amber500)
                                Text("Đã duyệt: ${project.approvedTimesheets ?: 0}", style = MaterialTheme.typography.labelSmall, color = Emerald500)
                            }
                        }
                    }
                }
            }
        }
    }
}
