package com.payroll.android.ui.admin.transactions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.payroll.android.data.remote.dto.Transaction
import com.payroll.android.ui.components.*
import com.payroll.android.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    viewModel: TransactionViewModel,
    onAddTransaction: () -> Unit,
    onTransactionClick: (Int) -> Unit
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) { viewModel.loadAll() }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Giao dịch", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White),
                actions = { IconButton(onClick = onAddTransaction) { Icon(Icons.Default.Add, null, tint = Sky500) } })
        }, containerColor = Gray50
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            Row(Modifier.padding(horizontal = 16.dp, vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip("Tất cả", state.statusFilter == null) { viewModel.onStatusFilter(null) }
                FilterChip("Chờ xử lý", state.statusFilter == "pending") { viewModel.onStatusFilter("pending") }
                FilterChip("Đã thanh toán", state.statusFilter == "settled") { viewModel.onStatusFilter("settled") }
                FilterChip("Đã đảo", state.statusFilter == "reversed") { viewModel.onStatusFilter("reversed") }
            }

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Sky500) }
            } else if (state.transactions.isEmpty()) {
                EmptyState(Icons.Default.Receipt, "Không có giao dịch")
            } else {
                LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.transactions, key = { it.id }) { tx ->
                        GlassCard(Modifier.fillMaxWidth()) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(Modifier.weight(1f)) {
                                    Text(tx.description ?: "", fontWeight = FontWeight.SemiBold, color = Gray800)
                                    Text("${tx.party ?: ""} · ${tx.transactionType ?: ""}", style = MaterialTheme.typography.bodySmall, color = Gray500)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(formatVnd(tx.amount ?: 0.0), fontWeight = FontWeight.Bold, color = if (tx.transactionType == "income") Emerald500 else Red500)
                                    StatusBadge(tx.status ?: "pending")
                                }
                            }
                        }
                    }
                    if (state.hasMore) item { LoadMoreButton(viewModel::loadMore, state.isLoadingMore) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(
    transactionId: Int,
    viewModel: TransactionViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(transactionId) { viewModel.loadAll() }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Chi tiết giao dịch", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White))
        }, containerColor = Gray50
    ) { padding ->
        val tx = state.transactions.find { it.id == transactionId }
        if (tx == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Sky500) }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                item {
                    GlassCard {
                        Text("Thông tin", fontWeight = FontWeight.Bold, color = Gray800)
                        Spacer(Modifier.height(8.dp))
                        InfoRow("Mô tả", tx.description ?: "")
                        InfoRow("Loại", tx.transactionType ?: "")
                        InfoRow("Số tiền", formatVnd(tx.amount ?: 0.0))
                        InfoRow("Đối tác", tx.party ?: "")
                        InfoRow("Trạng thái", tx.status ?: "")
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (tx.status == "pending") {
                            Button(onClick = { /* settle */ }, colors = ButtonDefaults.buttonColors(containerColor = Emerald500)) { Text("Thanh toán") }
                        }
                        OutlinedButton(onClick = { /* reverse */ }, colors = ButtonDefaults.outlinedButtonColors(contentColor = Red500)) { Text("Đảo giao dịch") }
                    }
                }
                tx.settlements?.let { settlements ->
                    if (settlements.isNotEmpty()) {
                        item { Text("Lịch sử thanh toán", fontWeight = FontWeight.Bold, color = Gray800) }
                        items(settlements) { s ->
                            GlassCard {
                                Text(formatVnd(s.amount ?: 0.0), fontWeight = FontWeight.SemiBold, color = Gray800)
                                s.settlementDate?.let { Text("Ngày: $it", style = MaterialTheme.typography.bodySmall, color = Gray500) }
                                s.paymentMethod?.let { Text("PP: $it", style = MaterialTheme.typography.bodySmall, color = Gray500) }
                                s.notes?.let { Text("Ghi chú: $it", style = MaterialTheme.typography.bodySmall, color = Gray500) }
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
fun TransactionFormScreen(
    viewModel: TransactionViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.formState.collectAsState()
    LaunchedEffect(state.success) { if (state.success) onBack() }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Thêm giao dịch", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White))
        }, containerColor = Gray50
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (state.error != null) Text(state.error!!, color = Red500, style = MaterialTheme.typography.bodySmall)
            Field("Mô tả *", state.description) { viewModel.onFormField("description", it) }
            Field("Số tiền *", state.amount) { viewModel.onFormField("amount", it) }
            Field("Đối tác", state.party) { viewModel.onFormField("party", it) }

            Text("Loại giao dịch", fontWeight = FontWeight.SemiBold, color = Gray600, style = MaterialTheme.typography.bodySmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("expense" to "Chi phí", "income" to "Thu nhập").forEach { (key, label) ->
                    Surface(shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp), color = if (state.transactionType == key) Sky500 else Gray100) {
                        TextButton(onClick = { viewModel.onFormField("transactionType", key) }) { Text(label, color = if (state.transactionType == key) White else Gray600) }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Button(onClick = { viewModel.saveTransaction(onBack) }, enabled = !state.isLoading, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = Sky500)) {
                if (state.isLoading) CircularProgressIndicator(Modifier.size(20.dp), color = White, strokeWidth = 2.dp)
                else Text("Tạo giao dịch", fontWeight = FontWeight.SemiBold)
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

@Composable
private fun Field(label: String, value: String, onChange: (String) -> Unit) {
    OutlinedTextField(value = value, onValueChange = onChange, label = { Text(label) }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
}

private fun formatVnd(amount: Double): String = String.format("%,.0fđ", amount)
