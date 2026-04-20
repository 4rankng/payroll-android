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
import com.payroll.android.data.remote.dto.Project
import com.payroll.android.ui.components.*
import com.payroll.android.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectListScreen(
    viewModel: ProjectViewModel,
    onAddProject: () -> Unit,
    onProjectClick: (Int) -> Unit
) {
    val state by viewModel.listState.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadProjects() }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text("Dự án", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White),
                actions = {
                    IconButton(onClick = onAddProject) { Icon(Icons.Default.Add, null, tint = Sky500) }
                }
            )
        },
        containerColor = Gray50
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            state.summary?.let { s ->
                Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard("Tổng", "${s.totalProjects}", modifier = Modifier.weight(1f))
                    StatCard("Hoạt động", "${s.activeProjects}", modifier = Modifier.weight(1f), color = Emerald500)
                    StatCard("Doanh thu", formatVnd(s.totalRevenue), modifier = Modifier.weight(1f))
                }
            }

            SearchBar(state.search, viewModel::onSearch, "Tìm dự án...", Modifier.padding(horizontal = 16.dp, vertical = 4.dp))

            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip("Tất cả", state.statusFilter == null) { viewModel.onStatusFilter(null) }
                FilterChip("Hoạt động", state.statusFilter == "active") { viewModel.onStatusFilter("active") }
                FilterChip("Hoàn thành", state.statusFilter == "completed") { viewModel.onStatusFilter("completed") }
                FilterChip("Tạm dừng", state.statusFilter == "paused") { viewModel.onStatusFilter("paused") }
            }

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Sky500) }
            } else if (state.projects.isEmpty()) {
                EmptyState(Icons.Default.Folder, "Không có dự án")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.projects, key = { it.id }) { project ->
                        ProjectCard(project = project, onClick = { onProjectClick(project.id) })
                    }
                    if (state.hasMore) {
                        item { LoadMoreButton(viewModel::loadMore, state.isLoadingMore) }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProjectCard(project: Project, onClick: () -> Unit) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(project.name ?: "N/A", fontWeight = FontWeight.SemiBold, color = Gray800)
                project.code?.let { Text("Mã: $it", style = MaterialTheme.typography.bodySmall, color = Gray500) }
                project.clientName?.let { Text("Khách hàng: $it", style = MaterialTheme.typography.bodySmall, color = Gray500) }
                project.employeeCount?.let { Text("$it nhân viên", style = MaterialTheme.typography.bodySmall, color = Sky600) }
            }
            StatusBadge(project.status ?: "active")
        }
    }
}

private fun formatVnd(amount: Double): String = String.format("%,.0fđ", amount)
