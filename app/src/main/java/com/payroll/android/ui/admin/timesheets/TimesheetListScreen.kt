package com.payroll.android.ui.admin.timesheets

import androidx.compose.foundation.clickable
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
import com.payroll.android.data.remote.dto.AdminTimesheet
import com.payroll.android.ui.components.*
import com.payroll.android.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimesheetListScreen(
    viewModel: AdminTimesheetViewModel,
    onAddTimesheet: () -> Unit,
    onEditTimesheet: (Int) -> Unit
) {
    val state by viewModel.state.collectAsState()
    var showDeleteConfirm by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text("Chấm công", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White),
                actions = {
                    if (state.summary?.pending ?: 0 > 0) {
                        IconButton(onClick = { viewModel.approveAll() }) {
                            Icon(Icons.Default.CheckCircle, null, tint = Emerald500)
                        }
                    }
                    IconButton(onClick = onAddTimesheet) { Icon(Icons.Default.Add, null, tint = Sky500) }
                }
            )
        },
        containerColor = Gray50
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            state.summary?.let { s ->
                Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatCard("Tổng", "${s.totalTimesheets}", modifier = Modifier.weight(1f))
                    StatCard("Chờ duyệt", "${s.pending}", modifier = Modifier.weight(1f), color = Amber500)
                    StatCard("Đã duyệt", "${s.approved}", modifier = Modifier.weight(1f), color = Emerald500)
                    StatCard("Tổng giờ", "${s.totalHours}h", modifier = Modifier.weight(1f))
                }
            }

            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip("Tất cả", state.statusFilter == null) { viewModel.onStatusFilter(null) }
                FilterChip("Chờ duyệt", state.statusFilter == "pending") { viewModel.onStatusFilter("pending") }
                FilterChip("Đã duyệt", state.statusFilter == "approved") { viewModel.onStatusFilter("approved") }
                FilterChip("Từ chối", state.statusFilter == "rejected") { viewModel.onStatusFilter("rejected") }
            }

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Sky500) }
            } else if (state.timesheets.isEmpty()) {
                EmptyState(Icons.Default.Schedule, "Không có chấm công")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.timesheets, key = { it.id }) { ts ->
                        val isSelected = state.selectedIds.contains(ts.id)
                        TimesheetCard(
                            timesheet = ts,
                            isSelected = isSelected,
                            onClick = { viewModel.showDetail(ts) },
                            onToggleSelect = { viewModel.toggleSelect(ts.id) },
                            onApprove = { viewModel.approveTimesheet(ts.id) },
                            onReject = { viewModel.showRejectDialog(ts.id) },
                            onEdit = { onEditTimesheet(ts.id) },
                            onDelete = { showDeleteConfirm = ts.id }
                        )
                    }
                    if (state.hasMore) {
                        item { LoadMoreButton(viewModel::loadMore, state.isLoadingMore) }
                    }
                }
            }
        }

        // Bulk action bar
        if (state.selectedIds.isNotEmpty()) {
            Box(Modifier.fillMaxSize()) {
                BulkActionBar(
                    selectedCount = state.selectedIds.size,
                    onApprove = { viewModel.bulkApprove() },
                    onReject = { viewModel.bulkReject() },
                    onClear = { viewModel.clearSelection() }
                )
            }
        }
    }

    // Detail sheet
    if (state.showDetail && state.selectedTimesheet != null) {
        val ts = state.selectedTimesheet!!
        ModalBottomSheet(onDismissRequest = { viewModel.dismissDetail() }) {
            Column(modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 32.dp)) {
                Text("Chi tiết chấm công", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                InfoRow("Nhân viên", ts.employeeName ?: "")
                InfoRow("Dự án", ts.projectName ?: "")
                InfoRow("Ngày", ts.date ?: "")
                InfoRow("Giờ làm", "${ts.hoursWorked ?: 0.0}h")
                InfoRow("Loại", ts.paytype ?: "")
                InfoRow("Trạng thái", ts.status ?: "")
                ts.note?.let { InfoRow("Ghi chú", it) }
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (ts.status == "pending") {
                        Button(onClick = { viewModel.approveTimesheet(ts.id); viewModel.dismissDetail() }, colors = ButtonDefaults.buttonColors(containerColor = Emerald500)) { Text("Duyệt") }
                        OutlinedButton(onClick = { viewModel.dismissDetail(); viewModel.showRejectDialog(ts.id) }) { Text("Từ chối", color = Red500) }
                    }
                }
            }
        }
    }

    // Reject dialog
    if (state.showRejectDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissRejectDialog() },
            title = { Text("Từ chối chấm công") },
            text = {
                OutlinedTextField(
                    value = state.rejectReason,
                    onValueChange = viewModel::onRejectReasonChange,
                    label = { Text("Lý do từ chối") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.confirmReject() }) { Text("Xác nhận", color = Red500) }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissRejectDialog() }) { Text("Hủy") }
            }
        )
    }

    showDeleteConfirm?.let { id ->
        ConfirmDialog("Xóa chấm công", "Bạn có chắc chắn xóa?", "Xóa", onConfirm = { viewModel.deleteTimesheet(id); showDeleteConfirm = null }, onDismiss = { showDeleteConfirm = null }, isDestructive = true)
    }
}

@Composable
private fun TimesheetCard(
    timesheet: AdminTimesheet,
    isSelected: Boolean,
    onClick: () -> Unit,
    onToggleSelect: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isSelected, onCheckedChange = { onToggleSelect() })
            Column(modifier = Modifier.weight(1f).clickable { onClick() }) {
                Text(timesheet.employeeName ?: "N/A", fontWeight = FontWeight.SemiBold, color = Gray800)
                Text("${timesheet.projectName ?: ""} · ${timesheet.date ?: ""}", style = MaterialTheme.typography.bodySmall, color = Gray500)
                Text("${timesheet.hoursWorked ?: 0.0}h", style = MaterialTheme.typography.bodySmall, color = Sky600)
            }
            StatusBadge(timesheet.status ?: "pending")
        }
        if (timesheet.status == "pending") {
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilledTonalButton(onClick = onApprove, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) { Text("Duyệt", style = MaterialTheme.typography.labelSmall) }
                OutlinedButton(onClick = onReject, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) { Text("Từ chối", color = Red500, style = MaterialTheme.typography.labelSmall) }
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
