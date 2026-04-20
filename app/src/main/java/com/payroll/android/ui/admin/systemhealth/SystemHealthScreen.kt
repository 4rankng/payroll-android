package com.payroll.android.ui.admin.systemhealth

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
fun SystemHealthScreen(
    viewModel: SystemHealthViewModel
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) { viewModel.loadAll() }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(title = { Text("Sức khỏe hệ thống", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White),
                actions = { IconButton(onClick = { viewModel.loadAll() }) { Icon(Icons.Default.Refresh, null, tint = Sky500) } })
        }, containerColor = Gray50
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Sky500) }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // API Metrics
                item { SectionHeader("API Tổng quan") }
                items(state.metrics) { m ->
                    GlassCard {
                        Text(m.endpoint ?: "", fontWeight = FontWeight.SemiBold, color = Gray800)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${m.totalRequests ?: 0} requests", style = MaterialTheme.typography.bodySmall, color = Gray500)
                            Text("${String.format("%.1f", m.avgLatencyMs ?: 0.0)}ms", style = MaterialTheme.typography.bodySmall, color = Sky600)
                            val errRate = m.errorRate ?: 0.0
                            Surface(shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp), color = if (errRate > 5) Red500.copy(alpha = 0.15f) else Emerald500.copy(alpha = 0.15f)) {
                                Text("${String.format("%.1f", errRate)}% lỗi", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), color = if (errRate > 5) Red500 else Emerald500, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                // Slowest endpoints
                if (state.slowestEndpoints.isNotEmpty()) {
                    item { SectionHeader("Endpoint chậm nhất") }
                    items(state.slowestEndpoints) { s ->
                        GlassCard {
                            Text(s.endpoint ?: "", fontWeight = FontWeight.SemiBold, color = Gray800)
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("TB: ${String.format("%.0f", s.avgLatencyMs ?: 0.0)}ms", style = MaterialTheme.typography.bodySmall, color = Amber500)
                                Text("Max: ${String.format("%.0f", s.maxLatencyMs ?: 0.0)}ms", style = MaterialTheme.typography.bodySmall, color = Red500)
                                Text("${s.requestCount ?: 0} req", style = MaterialTheme.typography.bodySmall, color = Gray500)
                            }
                        }
                    }
                }

                // Recent errors
                if (state.recentErrors.isNotEmpty()) {
                    item { SectionHeader("Lỗi gần đây") }
                    items(state.recentErrors) { e ->
                        GlassCard {
                            Text(e.endpoint ?: "", fontWeight = FontWeight.SemiBold, color = Gray800)
                            Text("${e.method ?: ""} → ${e.statusCode ?: 0}", style = MaterialTheme.typography.bodySmall, color = Red500)
                            e.message?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = Gray600, maxLines = 2) }
                            e.createdAt?.let { Text(formatDate(it), style = MaterialTheme.typography.labelSmall, color = Gray400) }
                        }
                    }
                }
            }
        }
    }
}

private fun formatDate(iso: String): String = try {
    val input = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault())
    val output = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale("vi"))
    output.format(input.parse(iso)!!)
} catch (_: Exception) { iso }
