package com.payroll.android.ui.admin.loans

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
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
import com.payroll.android.ui.components.*
import com.payroll.android.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanListScreen(
    viewModel: LoanViewModel,
    onAddLoan: () -> Unit,
    onLoanClick: (Int) -> Unit
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) { viewModel.loadAll() }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(title = { Text("Khoản vay", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White),
                actions = {
                    IconButton(onClick = { viewModel.showLenderDialog() }) { Icon(Icons.Default.PersonAdd, null, tint = Sky500) }
                    IconButton(onClick = onAddLoan) { Icon(Icons.Default.Add, null, tint = Sky500) }
                })
        }, containerColor = Gray50
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Sky500) }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Lenders section
                if (state.lenders.isNotEmpty()) {
                    item { SectionHeader("Người cho vay (${state.lenders.size})") }
                    items(state.lenders, key = { it.id }) { lender ->
                        GlassCard {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column { Text(lender.name ?: "", fontWeight = FontWeight.SemiBold, color = Gray800); lender.contact?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = Gray500) } }
                                IconButton(onClick = { viewModel.deleteLender(lender.id) }) { Icon(Icons.Default.Delete, null, tint = Red500, modifier = Modifier.size(18.dp)) }
                            }
                        }
                    }
                }

                item { SectionHeader("Khoản vay") }
                if (state.loans.isEmpty()) {
                    item { EmptyState(Icons.Default.AccountBalance, "Không có khoản vay") }
                } else {
                    items(state.loans, key = { it.id }) { loan ->
                        GlassCard {
                            Column {
                                Text(loan.lenderName ?: "", fontWeight = FontWeight.SemiBold, color = Gray800)
                                Text(formatVnd(loan.amount ?: 0.0), color = Sky600, fontWeight = FontWeight.Bold)
                                Text("Lãi suất: ${loan.interestRate ?: 0.0}% | ${loan.termMonths ?: 0} tháng", style = MaterialTheme.typography.bodySmall, color = Gray500)
                                StatusBadge(loan.status ?: "pending")
                            }
                        }
                    }
                    if (state.hasMore) item { LoadMoreButton(viewModel::loadMore, state.isLoadingMore) }
                }
            }
        }
    }

    // Lender dialog
    if (state.showLenderDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissLenderDialog() },
            title = { Text("Thêm người cho vay") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = state.lenderName, onValueChange = { viewModel.onLenderField("name", it) }, label = { Text("Tên *") }, singleLine = true)
                    OutlinedTextField(value = state.lenderContact, onValueChange = { viewModel.onLenderField("contact", it) }, label = { Text("Liên hệ") }, singleLine = true)
                }
            },
            confirmButton = { TextButton(onClick = { viewModel.createLender() }) { Text("Thêm") } },
            dismissButton = { TextButton(onClick = { viewModel.dismissLenderDialog() }) { Text("Hủy") } }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanDetailScreen(
    loanId: Int,
    viewModel: LoanViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val loan = state.loans.find { it.id == loanId }

    LaunchedEffect(loanId) { if (loan != null) viewModel.loadDetail(loan) }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(title = { Text("Chi tiết khoản vay", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White))
        }, containerColor = Gray50
    ) { padding ->
        if (loan == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { Text("Không tìm thấy") }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                item {
                    GlassCard {
                        Text("Thông tin khoản vay", fontWeight = FontWeight.Bold, color = Gray800)
                        Spacer(Modifier.height(8.dp))
                        InfoRow("Người cho vay", loan.lenderName ?: "")
                        InfoRow("Số tiền", formatVnd(loan.amount ?: 0.0))
                        InfoRow("Lãi suất", "${loan.interestRate ?: 0.0}%")
                        InfoRow("Kỳ hạn", "${loan.termMonths ?: 0} tháng")
                        InfoRow("Trạng thái", loan.status ?: "")
                        loan.remainingBalance?.let { InfoRow("Còn lại", formatVnd(it)) }
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (loan.status == "pending") {
                            Button(onClick = { viewModel.disburseLoan() }, colors = ButtonDefaults.buttonColors(containerColor = Emerald500)) { Text("Giải ngân") }
                        }
                        if (loan.status == "disbursed") {
                            Button(onClick = { viewModel.showRepayForm() }) { Text("Trả nợ") }
                        }
                    }
                }
                if (state.schedule.isNotEmpty()) {
                    item { SectionHeader("Lịch trả nợ") }
                    items(state.schedule, key = { it.month ?: 0 }) { s ->
                        GlassCard {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column { Text("Kỳ ${s.month}", fontWeight = FontWeight.SemiBold); Text("Hạn: ${s.dueDate ?: ""}", style = MaterialTheme.typography.bodySmall, color = Gray500) }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(formatVnd(s.paymentAmount ?: 0.0), fontWeight = FontWeight.SemiBold, color = Gray800)
                                    StatusBadge(s.status ?: "pending")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Repay form
    if (state.showRepayForm && loan != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissDetail() },
            title = { Text("Trả nợ") },
            text = { OutlinedTextField(value = state.repayAmount, onValueChange = { viewModel.onRepayAmountChange(it) }, label = { Text("Số tiền") }, singleLine = true) },
            confirmButton = { TextButton(onClick = { viewModel.repayLoan() }) { Text("Xác nhận") } },
            dismissButton = { TextButton(onClick = { viewModel.dismissDetail() }) { Text("Hủy") } }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanFormScreen(
    viewModel: LoanViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.formState.collectAsState()
    val listState by viewModel.state.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadAll() }
    LaunchedEffect(state.success) { if (state.success) onBack() }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(title = { Text("Thêm khoản vay", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White))
        }, containerColor = Gray50
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (state.error != null) Text(state.error!!, color = Red500, style = MaterialTheme.typography.bodySmall)

            // Lender selector
            Text("Người cho vay", fontWeight = FontWeight.SemiBold, color = Gray600, style = MaterialTheme.typography.bodySmall)
            listState.lenders.forEach { lender ->
                Surface(
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                    color = if (state.lenderId == lender.id.toString()) Sky500 else Gray100
                ) {
                    TextButton(onClick = { viewModel.onFormField("lenderId", lender.id.toString()) }) {
                        Text(lender.name ?: "", color = if (state.lenderId == lender.id.toString()) White else Gray600)
                    }
                }
            }

            Field("Số tiền *", state.amount) { viewModel.onFormField("amount", it) }
            Field("Lãi suất (%) *", state.interestRate) { viewModel.onFormField("interestRate", it) }
            Field("Kỳ hạn (tháng) *", state.termMonths) { viewModel.onFormField("termMonths", it) }
            Field("Ngày giải ngân (YYYY-MM-DD) *", state.disbursementDate) { viewModel.onFormField("disbursementDate", it) }

            Text("Loại vay", fontWeight = FontWeight.SemiBold, color = Gray600, style = MaterialTheme.typography.bodySmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("fixed" to "Cố định", "reducing" to "Giảm dần").forEach { (key, label) ->
                    Surface(shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp), color = if (state.loanType == key) Sky500 else Gray100) {
                        TextButton(onClick = { viewModel.onFormField("loanType", key) }) { Text(label, color = if (state.loanType == key) White else Gray600) }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Button(onClick = { viewModel.saveLoan(onBack) }, enabled = !state.isLoading, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = Sky500)) {
                if (state.isLoading) CircularProgressIndicator(Modifier.size(20.dp), color = White, strokeWidth = 2.dp)
                else Text("Tạo khoản vay", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) { Row(Modifier.fillMaxWidth().padding(vertical = 2.dp)) { Text("$label: ", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall, color = Gray600); Text(value, style = MaterialTheme.typography.bodySmall, color = Gray800) } }
@Composable
private fun Field(label: String, value: String, onChange: (String) -> Unit) { OutlinedTextField(value = value, onValueChange = onChange, label = { Text(label) }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)) }
private fun formatVnd(amount: Double): String = String.format("%,.0fđ", amount)
