package com.payroll.android.ui.admin.projects

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
import com.payroll.android.data.remote.dto.ProjectEmployee
import com.payroll.android.ui.components.*
import com.payroll.android.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    projectId: Int,
    viewModel: ProjectViewModel,
    onBack: () -> Unit,
    onEdit: (Int) -> Unit
) {
    val state by viewModel.detailState.collectAsState()

    LaunchedEffect(projectId) { viewModel.loadDetail(projectId) }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text(state.project?.name ?: "Chi tiết dự án", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White),
                actions = {
                    IconButton(onClick = { onEdit(projectId) }) { Icon(Icons.Default.Edit, null, tint = Sky500) }
                }
            )
        },
        containerColor = Gray50
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Sky500) }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Info
                item {
                    GlassCard {
                        Text("Thông tin dự án", fontWeight = FontWeight.Bold, color = Gray800)
                        Spacer(Modifier.height(8.dp))
                        state.project?.code?.let { InfoRow("Mã dự án", it) }
                        state.project?.clientName?.let { InfoRow("Khách hàng", it) }
                        state.project?.description?.let { InfoRow("Mô tả", it) }
                        state.project?.startDate?.let { InfoRow("Ngày bắt đầu", it) }
                        state.project?.endDate?.let { InfoRow("Ngày kết thúc", it) }
                        state.project?.status?.let { InfoRow("Trạng thái", it) }
                    }
                }

                // Financial
                state.financial?.let { fin ->
                    item {
                        GlassCard {
                            Text("Tài chính", fontWeight = FontWeight.Bold, color = Gray800)
                            Spacer(Modifier.height(8.dp))
                            InfoRow("Doanh thu", formatVnd(fin.revenue ?: 0.0))
                            InfoRow("Chi phí", formatVnd(fin.expenses ?: 0.0))
                            InfoRow("Lợi nhuận", formatVnd(fin.profit ?: 0.0))
                            fin.totalHours?.let { InfoRow("Tổng giờ", "${it}h") }
                        }
                    }
                }

                // Employees
                item { SectionHeader("Nhân viên (${state.employees.size})") }
                if (state.employees.isEmpty()) {
                    item { EmptyState(Icons.Default.People, "Chưa có nhân viên") }
                } else {
                    items(state.employees, key = { it.id }) { emp ->
                        GlassCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(emp.fullname ?: "N/A", fontWeight = FontWeight.SemiBold, color = Gray800)
                                    emp.role?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = Gray500) }
                                }
                                Row {
                                    StatusBadge(emp.status ?: "active")
                                    Spacer(Modifier.width(8.dp))
                                    IconButton(onClick = { viewModel.removeEmployeeFromProject(projectId, emp.id) }) {
                                        Icon(Icons.Default.Close, null, tint = Red500, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Text("$label: ", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall, color = Gray600)
        Text(value, style = MaterialTheme.typography.bodySmall, color = Gray800)
    }
}

private fun formatVnd(amount: Double): String = String.format("%,.0fđ", amount)
