package com.payroll.android.ui.admin.advance

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
fun AdvancePaymentListScreen(
    viewModel: AdvancePaymentAdminViewModel
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) { viewModel.loadAll() }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Ứng lương", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White),
                actions = { IconButton(onClick = { viewModel.loadAll() }) { Icon(Icons.Default.Refresh, null, tint = Sky500) } })
        }, containerColor = Gray50
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            state.summary?.let { s ->
                Row(Modifier.padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatCard("Chờ duyệt", "${s.pendingCount}", Modifier.weight(1f), color = Amber500)
                    StatCard("Đã duyệt", "${s.approvedCount}", Modifier.weight(1f), color = Emerald500)
                    StatCard("Hoàn thành", "${s.completedCount}", Modifier.weight(1f))
                    StatCard("Đã hủy", "${s.cancelledCount}", Modifier.weight(1f), color = Red500)
                }
            }

            Row(Modifier.padding(horizontal = 16.dp, vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip("Tất cả", state.statusFilter == null) { viewModel.onStatusFilter(null) }
                FilterChip("Chờ duyệt", state.statusFilter == "PENDING") { viewModel.onStatusFilter("PENDING") }
                FilterChip("Đã duyệt", state.statusFilter == "APPROVED") { viewModel.onStatusFilter("APPROVED") }
                FilterChip("Hoàn thành", state.statusFilter == "COMPLETED") { viewModel.onStatusFilter("COMPLETED") }
            }

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Sky500) }
            } else if (state.advancePayments.isEmpty()) {
                EmptyState(Icons.Default.Payments, "Không có ứng lương")
            } else {
                LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.advancePayments, key = { it.id }) { ap ->
                        GlassCard {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(Modifier.weight(1f)) {
                                    Text(ap.employeeName ?: "", fontWeight = FontWeight.SemiBold, color = Gray800)
                                    Text(formatVnd(ap.amount ?: 0.0), color = Sky600, fontWeight = FontWeight.Bold)
                                    ap.forMonth?.let { Text("Tháng: $it", style = MaterialTheme.typography.bodySmall, color = Gray500) }
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    StatusBadge(ap.status ?: "PENDING")
                                    if (ap.status == "PENDING" || ap.status == "APPROVED") {
                                        Spacer(Modifier.height(4.dp))
                                        TextButton(onClick = { viewModel.showCancelConfirm(ap.id) }) { Text("Hủy", color = Red500, style = MaterialTheme.typography.labelSmall) }
                                    }
                                }
                            }
                        }
                    }
                    if (state.hasMore) item { LoadMoreButton(viewModel::loadMore, state.isLoadingMore) }
                }
            }
        }
    }

    if (state.showCancelConfirm) {
        ConfirmDialog("Hủy ứng lương", "Bạn có chắc chắn hủy?", "Hủy", onConfirm = { viewModel.cancelPayment() }, onDismiss = { viewModel.dismissCancelConfirm() }, isDestructive = true)
    }
}

private fun formatVnd(amount: Double): String = String.format("%,.0fđ", amount)
