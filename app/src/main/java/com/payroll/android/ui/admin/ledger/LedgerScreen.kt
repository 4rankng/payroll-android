package com.payroll.android.ui.admin.ledger

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
fun LedgerScreen(
    viewModel: LedgerViewModel
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) { viewModel.loadAll() }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Sổ cái", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White),
                actions = { IconButton(onClick = { viewModel.loadAll() }) { Icon(Icons.Default.Refresh, null, tint = Sky500) } })
        }, containerColor = Gray50
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Balance cards
            state.balance?.let { b ->
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard("Tổng nợ", formatVnd(b.totalDebit), modifier = Modifier.weight(1f), color = Red500)
                        StatCard("Tổng có", formatVnd(b.totalCredit), modifier = Modifier.weight(1f), color = Emerald500)
                    }
                }
                item { StatCard("Số dư", formatVnd(b.balance), color = if (b.balance >= 0) Emerald500 else Red500) }
            }

            // Entries
            item { SectionHeader("Bút toán") }
            if (state.isLoading) {
                item { Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Sky500) } }
            } else if (state.entries.isEmpty()) {
                item { EmptyState(Icons.Default.Book, "Không có bút toán") }
            } else {
                items(state.entries, key = { it.id }) { entry ->
                    GlassCard {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(Modifier.weight(1f)) {
                                Text(entry.description ?: "", fontWeight = FontWeight.SemiBold, color = Gray800)
                                Text(entry.accountType ?: "", style = MaterialTheme.typography.bodySmall, color = Gray500)
                                entry.createdAt?.let { Text(formatDate(it), style = MaterialTheme.typography.labelSmall, color = Gray400) }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                entry.debitAmount?.let { if (it > 0) Text("Nợ: ${formatVnd(it)}", color = Red500, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall) }
                                entry.creditAmount?.let { if (it > 0) Text("Có: ${formatVnd(it)}", color = Emerald500, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall) }
                            }
                        }
                        if (entry.status != "reversed") {
                            Spacer(Modifier.height(4.dp))
                            TextButton(onClick = { viewModel.showReverseConfirm(entry.id) }) {
                                Text("Đảo bút toán", color = Red500, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
                if (state.hasMore) item { LoadMoreButton(viewModel::loadMore, state.isLoadingMore) }
            }
        }
    }

    if (state.showReverseConfirm) {
        ConfirmDialog("Đảo bút toán", "Bạn có chắc chắn đảo bút toán này?", "Đảo", onConfirm = { viewModel.reverseEntry() }, onDismiss = { viewModel.dismissReverseConfirm() }, isDestructive = true)
    }
}

private fun formatVnd(amount: Double): String = String.format("%,.0fđ", amount)
private fun formatDate(iso: String): String = try {
    val input = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault())
    val output = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale("vi"))
    output.format(input.parse(iso)!!)
} catch (_: Exception) { iso }
