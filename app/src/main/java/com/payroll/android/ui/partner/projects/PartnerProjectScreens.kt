package com.payroll.android.ui.partner.projects

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
fun PartnerProjectListScreen(
    viewModel: PartnerProjectViewModel,
    onProjectClick: (Int) -> Unit
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) { viewModel.loadProjects() }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = { TopAppBar(title = { Text("Dự án", fontWeight = FontWeight.Bold) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = White)) },
        containerColor = Gray50
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Sky500) }
        } else if (state.projects.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { EmptyState(Icons.Default.Folder, "Không có dự án") }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.projects, key = { it.id }) { project ->
                    GlassCard {
                        Column {
                            Text(project.name ?: "", fontWeight = FontWeight.SemiBold, color = Gray800)
                            project.code?.let { Text("Mã: $it", style = MaterialTheme.typography.bodySmall, color = Gray500) }
                            Spacer(Modifier.height(4.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("${project.employeeCount ?: 0} nhân viên", style = MaterialTheme.typography.bodySmall, color = Sky600)
                                StatusBadge(project.status ?: "active")
                            }
                            // Timesheet counts only - NO amounts
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Text("Chờ duyệt: ${project.pendingTimesheets ?: 0}", style = MaterialTheme.typography.labelSmall, color = Amber500)
                                Text("Đã duyệt: ${project.approvedTimesheets ?: 0}", style = MaterialTheme.typography.labelSmall, color = Emerald500)
                            }
                            if (project.totalHours != null) {
                                Text("Tổng giờ: ${project.totalHours}h", style = MaterialTheme.typography.labelSmall, color = Gray500)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartnerProjectDetailScreen(
    projectId: Int,
    viewModel: PartnerProjectViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(projectId) { viewModel.loadDetail(projectId) }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text(state.selectedProject?.name ?: "Chi tiết dự án", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
            )
        }, containerColor = Gray50
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Sky500) }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                state.selectedProject?.let { p ->
                    item {
                        GlassCard {
                            Text("Thông tin dự án", fontWeight = FontWeight.Bold, color = Gray800)
                            Spacer(Modifier.height(8.dp))
                            p.code?.let { InfoRow("Mã dự án", it) }
                            p.clientName?.let { InfoRow("Khách hàng", it) }
                            p.startDate?.let { InfoRow("Ngày bắt đầu", it) }
                            p.endDate?.let { InfoRow("Ngày kết thúc", it) }
                            InfoRow("Trạng thái", p.status ?: "")
                            InfoRow("Nhân viên", "${state.projectEmployees.size}")
                        }
                    }
                }

                // Employees - NO salary, NO bank info
                item { SectionHeader("Nhân viên (${state.projectEmployees.size})") }
                if (state.projectEmployees.isEmpty()) {
                    item { EmptyState(Icons.Default.People, "Chưa có nhân viên") }
                } else {
                    items(state.projectEmployees, key = { it.id }) { emp ->
                        GlassCard {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(emp.fullname ?: "", fontWeight = FontWeight.SemiBold, color = Gray800)
                                StatusBadge(emp.status ?: "active")
                            }
                            emp.role?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = Gray500) }
                        }
                    }
                }

                // Timesheet status overview - NO payment amounts
                if (state.projectTimesheets.isNotEmpty()) {
                    item {
                        val pending = state.projectTimesheets.count { it.status == "pending" }
                        val approved = state.projectTimesheets.count { it.status == "approved" }
                        val rejected = state.projectTimesheets.count { it.status == "rejected" }
                        val totalHours = state.projectTimesheets.sumOf { it.hoursWorked ?: 0.0 }
                        GlassCard {
                            Text("Trạng thái chấm công", fontWeight = FontWeight.Bold, color = Gray800)
                            Spacer(Modifier.height(8.dp))
                            InfoRow("Tổng giờ", "${totalHours}h")
                            InfoRow("Chờ duyệt", "$pending")
                            InfoRow("Đã duyệt", "$approved")
                            InfoRow("Từ chối", "$rejected")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Text("$label: ", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall, color = Gray600)
        Text(value, style = MaterialTheme.typography.bodySmall, color = Gray800)
    }
}
