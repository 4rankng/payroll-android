package com.payroll.android.ui.admin.dashboard

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
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadAll() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tổng quan", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White),
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, null, tint = Sky500)
                    }
                }
            )
        },
        containerColor = Gray50
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Sky500)
            }
        } else if (state.error != null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                EmptyState(Icons.Default.Error, state.error ?: "Lỗi tải dữ liệu")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Summary cards
                item {
                    val s = state.summary
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard("Tổng nhân viên", "${s?.totalEmployees ?: 0}", modifier = Modifier.weight(1f))
                        StatCard("Đang làm việc", "${s?.activeEmployees ?: 0}", modifier = Modifier.weight(1f), color = Emerald500)
                    }
                }
                item {
                    val s = state.summary
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard("Dự án", "${s?.totalProjects ?: 0}", modifier = Modifier.weight(1f))
                        StatCard("Chờ duyệt", "${s?.pendingApprovals ?: 0}", modifier = Modifier.weight(1f), color = Amber500)
                    }
                }
                item {
                    val s = state.summary
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard("Doanh thu", formatVnd(s?.monthlyRevenue ?: 0.0), modifier = Modifier.weight(1f), color = Emerald500)
                        StatCard("Chi phí", formatVnd(s?.monthlyExpenses ?: 0.0), modifier = Modifier.weight(1f), color = Red500)
                    }
                }
                item {
                    val s = state.summary
                    StatCard("Lợi nhuận", formatVnd(s?.netProfit ?: 0.0), color = if ((s?.netProfit ?: 0.0) >= 0) Emerald500 else Red500)
                }

                // Pending timesheets
                state.summary?.let { s ->
                    if (s.totalPendingTimesheets > 0) {
                        item {
                            SectionHeader("Chấm công chờ duyệt", "Xem tất cả")
                        }
                        item {
                            GlassCard {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Chờ duyệt", color = Amber500, fontWeight = FontWeight.SemiBold)
                                    Text("${s.totalPendingTimesheets} bảng", color = Gray600)
                                }
                                Spacer(Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Đã duyệt", color = Emerald500, fontWeight = FontWeight.SemiBold)
                                    Text("${s.totalApprovedTimesheets} bảng", color = Gray600)
                                }
                            }
                        }
                    }
                }

                // Project profitability
                if (state.projectProfitability.isNotEmpty()) {
                    item { SectionHeader("Lợi nhuận dự án") }
                    items(state.projectProfitability) { p ->
                        GlassCard {
                            Text(p.project ?: "", fontWeight = FontWeight.SemiBold, color = Gray800)
                            Spacer(Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Doanh thu: ${formatVnd(p.revenue ?: 0.0)}", style = MaterialTheme.typography.bodySmall, color = Gray600)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Chi phí: ${formatVnd(p.expenses ?: 0.0)}", style = MaterialTheme.typography.bodySmall, color = Gray600)
                                val profit = p.profit ?: 0.0
                                Text("Lợi nhuận: ${formatVnd(profit)}", style = MaterialTheme.typography.bodySmall, color = if (profit >= 0) Emerald500 else Red500, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                // Recent activities
                if (state.recentActivities.isNotEmpty()) {
                    item { SectionHeader("Hoạt động gần đây") }
                    items(state.recentActivities) { a ->
                        GlassCard {
                            Text(a.action ?: "", fontWeight = FontWeight.SemiBold, color = Gray800)
                            a.user?.let { Text("Người dùng: $it", style = MaterialTheme.typography.bodySmall, color = Gray600) }
                            a.target?.let { Text("Đối tượng: $it", style = MaterialTheme.typography.bodySmall, color = Gray600) }
                            a.createdAt?.let { Text(formatDate(it), style = MaterialTheme.typography.labelSmall, color = Gray400) }
                        }
                    }
                }

                // Salary distribution
                if (state.salaryDistribution.isNotEmpty()) {
                    item { SectionHeader("Phân bổ lương") }
                    items(state.salaryDistribution) { s ->
                        GlassCard {
                            Text(s.employee ?: "", fontWeight = FontWeight.SemiBold, color = Gray800)
                            Text(formatVnd(s.amount ?: 0.0), color = Sky600, fontWeight = FontWeight.Bold)
                            s.project?.let { Text("Dự án: $it", style = MaterialTheme.typography.bodySmall, color = Gray500) }
                        }
                    }
                }

                item { Spacer(Modifier.height(32.dp)) }
            }
        }
    }
}

private fun formatVnd(amount: Double): String = String.format("%,.0fđ", amount)

private fun formatDate(iso: String): String = try {
    val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    val output = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("vi"))
    output.format(input.parse(iso)!!)
} catch (_: Exception) { iso }
