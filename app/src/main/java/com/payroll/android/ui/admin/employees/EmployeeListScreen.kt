package com.payroll.android.ui.admin.employees

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
import com.payroll.android.data.remote.dto.AdminEmployee
import com.payroll.android.ui.components.*
import com.payroll.android.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeListScreen(
    viewModel: EmployeeViewModel,
    onAddEmployee: () -> Unit,
    onEditEmployee: (Int) -> Unit
) {
    val state by viewModel.state.collectAsState()
    var showDeleteConfirm by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text("Nhân viên", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White),
                actions = {
                    if (state.missingBankDetails.isNotEmpty()) {
                        IconButton(onClick = { viewModel.toggleMissingBank() }) {
                            Icon(Icons.Default.Warning, null, tint = Amber500)
                        }
                    }
                    IconButton(onClick = onAddEmployee) {
                        Icon(Icons.Default.Add, null, tint = Sky500)
                    }
                }
            )
        },
        containerColor = Gray50
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Stats
            state.summary?.let { s ->
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard("Tổng", "${s.totalEmployees}", modifier = Modifier.weight(1f))
                    StatCard("Đang làm", "${s.totalWorkingEmployees}", modifier = Modifier.weight(1f), color = Emerald500)
                    StatCard("Mới tháng này", "${s.employeesHiredThisMonth}", modifier = Modifier.weight(1f), color = Sky600)
                }
            }

            // Search
            SearchBar(
                query = state.search,
                onQueryChange = viewModel::onSearch,
                placeholder = "Tìm nhân viên...",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            // Status filter
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip("Tất cả", state.statusFilter == null) { viewModel.onStatusFilter(null) }
                FilterChip("Đang làm", state.statusFilter == "working") { viewModel.onStatusFilter("working") }
                FilterChip("Chưa phân công", state.statusFilter == "unassigned") { viewModel.onStatusFilter("unassigned") }
            }

            // Missing bank details banner
            if (state.showMissingBank && state.missingBankDetails.isNotEmpty()) {
                Surface(
                    color = Amber500.copy(alpha = 0.1f),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Thiếu thông tin ngân hàng (${state.missingBankDetails.size})", fontWeight = FontWeight.SemiBold, color = Amber700)
                        state.missingBankDetails.take(3).forEach { b ->
                            Text("• ${b.employeeName ?: ""}", style = MaterialTheme.typography.bodySmall, color = Gray600)
                        }
                    }
                }
            }

            // Employee list
            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Sky500) }
            } else if (state.employees.isEmpty()) {
                EmptyState(Icons.Default.People, "Không có nhân viên")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.employees, key = { it.id }) { emp ->
                        EmployeeCard(
                            employee = emp,
                            onClick = { viewModel.selectEmployee(emp) },
                            onEdit = { onEditEmployee(emp.id) },
                            onDelete = { showDeleteConfirm = emp.id }
                        )
                    }
                    if (state.hasMore) {
                        item { LoadMoreButton(viewModel::loadMore, state.isLoadingMore) }
                    }
                }
            }
        }
    }

    // Employee detail sheet
    if (state.showDetail && state.selectedEmployee != null) {
        EmployeeDetailSheet(
            employee = state.selectedEmployee!!,
            projects = state.employeeProjects,
            timesheetSummary = state.employeeTimesheetSummary,
            payroll = state.employeePayroll,
            onDismiss = viewModel::dismissDetail,
            onEdit = { onEditEmployee(state.selectedEmployee!!.id); viewModel.dismissDetail() }
        )
    }

    // Delete confirm
    showDeleteConfirm?.let { id ->
        ConfirmDialog(
            title = "Xóa nhân viên",
            message = "Bạn có chắc chắn muốn xóa nhân viên này?",
            confirmLabel = "Xóa",
            onConfirm = { viewModel.deleteEmployee(id); showDeleteConfirm = null },
            onDismiss = { showDeleteConfirm = null },
            isDestructive = true
        )
    }
}

@Composable
private fun EmployeeCard(
    employee: AdminEmployee,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(employee.fullname ?: "N/A", fontWeight = FontWeight.SemiBold, color = Gray800)
                employee.email?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = Gray500) }
                employee.mobile?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = Gray500) }
                Spacer(Modifier.height(4.dp))
                StatusBadge(employee.status ?: "active")
            }
            Column {
                IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, null, tint = Sky500, modifier = Modifier.size(20.dp)) }
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null, tint = Red500, modifier = Modifier.size(20.dp)) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmployeeDetailSheet(
    employee: AdminEmployee,
    projects: List<com.payroll.android.data.remote.dto.EmployeeProject>,
    timesheetSummary: com.payroll.android.data.remote.dto.EmployeeTimesheetSummary?,
    payroll: List<com.payroll.android.data.remote.dto.PayrollEntry>,
    onDismiss: () -> Unit,
    onEdit: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        LazyColumn(
            modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(employee.fullname ?: "", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    FilledTonalButton(onClick = onEdit) { Text("Sửa") }
                }
            }
            item {
                StatusBadge(employee.status ?: "active")
            }
            item { DividerInfo("Email", employee.email) }
            item { DividerInfo("SĐT", employee.mobile) }
            item { DividerInfo("CCCD", employee.cccd) }
            item { DividerInfo("Địa chỉ", employee.address) }
            item { DividerInfo("Ngân hàng", employee.bank) }
            item { DividerInfo("STK", employee.bankAccountNumber) }

            if (projects.isNotEmpty()) {
                item { Text("Dự án", fontWeight = FontWeight.SemiBold, color = Gray800) }
                items(projects) { p ->
                    Text("• ${p.name} (${p.status})", style = MaterialTheme.typography.bodySmall, color = Gray600)
                }
            }

            timesheetSummary?.let { ts ->
                item {
                    Text("Thống kê chấm công", fontWeight = FontWeight.SemiBold, color = Gray800)
                    Text("Tổng giờ: ${ts.totalHours}h | ${ts.totalDays} ngày", style = MaterialTheme.typography.bodySmall, color = Gray600)
                    Text("Tổng lương: ${String.format("%,.0fđ", ts.totalSalary)}", style = MaterialTheme.typography.bodySmall, color = Sky600)
                }
            }
        }
    }
}

@Composable
private fun DividerInfo(label: String, value: String?) {
    if (value != null) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("$label: ", fontWeight = FontWeight.SemiBold, color = Gray600, style = MaterialTheme.typography.bodySmall)
            Text(value, color = Gray800, style = MaterialTheme.typography.bodySmall)
        }
    }
}
