package com.payroll.android.ui.partner.timesheets

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
import com.payroll.android.data.remote.dto.AdminTimesheet
import com.payroll.android.ui.components.*
import com.payroll.android.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartnerTimesheetScreen(
    viewModel: PartnerTimesheetViewModel
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) { viewModel.loadAll() }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Chấm công", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White),
                actions = { IconButton(onClick = { viewModel.loadAll() }) { Icon(Icons.Default.Refresh, null, tint = Sky500) } })
        }, containerColor = Gray50
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Sky500) }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Summary - only counts and hours, NO amounts
                state.summary?.let { s ->
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StatCard("Tổng", "${s.totalTimesheets}", Modifier.weight(1f))
                            StatCard("Chờ duyệt", "${s.pending}", Modifier.weight(1f), color = Amber500)
                            StatCard("Đã duyệt", "${s.approved}", Modifier.weight(1f), color = Emerald500)
                            StatCard("Tổng giờ", "${s.totalHours}h", Modifier.weight(1f))
                        }
                    }
                }

                // Grouped by employee - NO payment amounts, NO salary, NO payrate
                if (state.groupedTimesheets.isEmpty()) {
                    item { EmptyState(Icons.Default.Schedule, "Không có chấm công") }
                } else {
                    state.groupedTimesheets.forEach { group ->
                        item {
                            SectionHeader(group.employeeName ?: "Nhân viên")
                        }
                        group.entries?.forEach { entry ->
                            item {
                                GlassCard {
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Column(Modifier.weight(1f)) {
                                            Text(entry.date ?: "", fontWeight = FontWeight.Medium, color = Gray800)
                                            Text(entry.projectName ?: "", style = MaterialTheme.typography.bodySmall, color = Gray500)
                                            Text("${entry.hoursWorked ?: 0.0} giờ làm", style = MaterialTheme.typography.bodySmall, color = Sky600)
                                        }
                                        StatusBadge(entry.status ?: "pending")
                                    }
                                    // NO amount, NO salary, NO payrate shown
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
