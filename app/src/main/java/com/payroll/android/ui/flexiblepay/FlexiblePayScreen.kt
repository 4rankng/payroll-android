package com.payroll.android.ui.flexiblepay

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.payroll.android.data.remote.dto.AdvancePaymentHistoryItem
import com.payroll.android.ui.components.*
import com.payroll.android.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlexiblePayScreen(
    onLogout: () -> Unit,
    viewModel: FlexiblePayViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.successMessage) {
        state.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSuccess()
        }
    }
    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    if (state.showNotificationSheet) {
        NotificationSheet(
            notifications = state.notifications,
            onDismiss = { viewModel.showNotificationSheet(false) },
            onMarkRead = { viewModel.markNotificationRead(it) },
            onMarkAllRead = { viewModel.markAllRead() }
        )
    }

    if (state.showPasswordSheet) {
        PasswordSheet(
            onDismiss = { viewModel.showPasswordSheet(false) },
            onSubmit = { cur, new -> viewModel.changePassword(cur, new) },
            isLoading = state.isChangingPassword,
            error = state.passwordError,
            success = state.passwordSuccess
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = SkyBlue50
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(SkyBlue50, SkyBlue100, SkyBlue50)
                    )
                )
                .padding(padding)
        ) {
            if (state.isLoading) {
                LoadingSkeleton()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header
                    item {
                        HeaderBar(
                            greeting = viewModel.getGreeting(),
                            name = state.profile?.fullname ?: "bạn",
                            unreadCount = state.unreadCount,
                            onNotificationClick = { viewModel.showNotificationSheet(true) },
                            onPasswordClick = { viewModel.showPasswordSheet(true) },
                            onLogoutClick = { viewModel.logout(); onLogout() }
                        )
                    }

                    // Advance Limit Card
                    state.advanceInfo?.let { info ->
                        item {
                            AdvanceLimitCard(
                                info = info,
                                remainingRatio = if (info.maxAdvanceAmount > 0)
                                    (info.remainingAmount / info.maxAdvanceAmount).toFloat().coerceIn(0f, 1f) else 0f
                            )
                        }

                        // Completed + Pending Grid
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                InfoMiniCard(
                                    title = "Đã ứng",
                                    value = formatCurrency(info.completedAmount),
                                    modifier = Modifier.weight(1f),
                                    color = Emerald500
                                )
                                InfoMiniCard(
                                    title = "Đang chờ",
                                    value = formatCurrency(info.pendingAmount),
                                    modifier = Modifier.weight(1f),
                                    color = Amber500
                                )
                            }
                        }

                        // Request Form
                        item {
                            RequestFormCard(
                                remainingAmount = info.remainingAmount,
                                requestAmount = state.requestAmount,
                                sliderAmount = state.sliderAmount,
                                feeCalculation = state.feeCalculation,
                                isSubmitting = state.isSubmitting,
                                canRequest = info.canRequest,
                                onAmountChange = { viewModel.onAmountChange(it) },
                                onSliderChange = { viewModel.onSliderChange(it) },
                                onSubmit = { viewModel.submitRequest() }
                            )
                        }
                    }

                    // History Header
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.History,
                                contentDescription = null,
                                tint = SkyBlue600,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Lịch sử ứng lương",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Slate800
                            )
                        }
                    }

                    // History Items
                    items(state.history) { item ->
                        HistoryItemCard(
                            item = item,
                            isCancelling = state.cancellingId == item.id,
                            onCancel = { viewModel.cancelRequest(item.id) }
                        )
                    }

                    // Load More
                    if (state.hasMoreHistory) {
                        item {
                            TextButton(
                                onClick = { viewModel.loadMoreHistory() },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                if (state.isLoadingHistory) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                } else {
                                    Text("Xem thêm")
                                }
                            }
                        }
                    }

                    item { Spacer(Modifier.height(32.dp)) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HeaderBar(
    greeting: String,
    name: String,
    unreadCount: Int,
    onNotificationClick: () -> Unit,
    onPasswordClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = GlassWhite,
        shadowElevation = 2.dp,
        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    greeting,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp
                    ),
                    color = SkyBlue600
                )
                Text(
                    name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Slate800,
                    maxLines = 1
                )
            }

            // Notification Bell
            IconButton(onClick = onNotificationClick) {
                BadgedBox(
                    badge = {
                        if (unreadCount > 0) {
                            Badge {
                                Text(if (unreadCount > 9) "9+" else "$unreadCount")
                            }
                        }
                    }
                ) {
                    Icon(Icons.Default.Notifications, "Thông báo", tint = Slate600)
                }
            }

            // Profile Menu
            var menuExpanded by remember { mutableStateOf(false) }
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    AvatarInitials(name = name, size = 32.dp)
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Đổi mật khẩu") },
                        onClick = { menuExpanded = false; onPasswordClick() },
                        leadingIcon = { Icon(Icons.Default.Settings, null, tint = Slate500) }
                    )
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text("Đăng xuất", color = Red500) },
                        onClick = { menuExpanded = false; onLogoutClick() },
                        leadingIcon = { Icon(Icons.Default.Logout, null, tint = Red500) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AdvanceLimitCard(info: com.payroll.android.data.remote.dto.AdvancePaymentInfo, remainingRatio: Float) {
    GlassCard {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.TrendingUp, null, tint = SkyBlue600, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Hạn mức ứng lương", fontWeight = FontWeight.Bold, color = Slate800)
                }
                val monthLabel = info.forMonth?.let {
                    try {
                        val sdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())
                        val date = sdf.parse(it)
                        SimpleDateFormat("MMMM yyyy", Locale("vi")).format(date!!)
                    } catch (_: Exception) { it }
                } ?: "—"
                Text(
                    monthLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate500
                )
            }
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Còn lại", style = MaterialTheme.typography.labelSmall, color = Slate400)
                    Text(
                        formatCurrency(info.remainingAmount),
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = SkyBlue600
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Tối đa", style = MaterialTheme.typography.labelSmall, color = Slate400)
                    Text(
                        formatCurrency(info.maxAdvanceAmount),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = Slate600
                    )
                }
            }
            Spacer(Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { remainingRatio },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = SkyBlue500,
                trackColor = SkyBlue100,
            )
        }
    }
}

@Composable
private fun InfoMiniCard(title: String, value: String, modifier: Modifier = Modifier, color: androidx.compose.ui.graphics.Color) {
    GlassCard(modifier = modifier) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
            )
            Spacer(Modifier.width(8.dp))
            Column {
                Text(title, style = MaterialTheme.typography.labelSmall, color = Slate500)
                Text(value, fontWeight = FontWeight.Bold, color = Slate800, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun RequestFormCard(
    remainingAmount: Double,
    requestAmount: String,
    sliderAmount: Float,
    feeCalculation: com.payroll.android.data.remote.dto.FeeCalculationResponse?,
    isSubmitting: Boolean,
    canRequest: Boolean,
    onAmountChange: (String) -> Unit,
    onSliderChange: (Float) -> Unit,
    onSubmit: () -> Unit
) {
    GlassCard {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Yêu cầu ứng lương", fontWeight = FontWeight.Bold, color = Slate800, fontSize = 16.sp)
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = requestAmount,
                onValueChange = onAmountChange,
                label = { Text("Số tiền (VND)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                enabled = canRequest && !isSubmitting
            )
            Spacer(Modifier.height(8.dp))

            Slider(
                value = sliderAmount,
                onValueChange = onSliderChange,
                modifier = Modifier.fillMaxWidth(),
                enabled = canRequest && !isSubmitting,
                colors = SliderDefaults.colors(
                    thumbColor = SkyBlue600,
                    activeTrackColor = SkyBlue500
                )
            )
            Spacer(Modifier.height(8.dp))

            // Fee breakdown
            feeCalculation?.let { fee ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = SkyBlue50
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        FeeRow("Số tiền yêu cầu", formatCurrency(requestAmount.toDoubleOrNull() ?: 0.0))
                        FeeRow("Phí (${fee.fee / (requestAmount.toDoubleOrNull()?.takeIf { it > 0 } ?: 1.0) * 100}%)", formatCurrency(fee.fee))
                        HorizontalDivider(color = SkyBlue200, modifier = Modifier.padding(vertical = 4.dp))
                        FeeRow("Thực nhận", formatCurrency(fee.netAmount), valueColor = SkyBlue600)
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            Button(
                onClick = onSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = canRequest && !isSubmitting && requestAmount.toDoubleOrNull()?.let { it >= 10000 } == true,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SkyBlue500)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Gửi yêu cầu", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun FeeRow(label: String, value: String, valueColor: androidx.compose.ui.graphics.Color = Slate700) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = Slate500)
        Text(value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = valueColor)
    }
}

@Composable
private fun HistoryItemCard(
    item: AdvancePaymentHistoryItem,
    isCancelling: Boolean,
    onCancel: () -> Unit
) {
    val statusConfig = when (item.status) {
        "PENDING" -> StatusConfig(Amber400, Amber700, "Chờ xử lý")
        "APPROVED", "COMPLETED" -> StatusConfig(Emerald500, Emerald700, if (item.status == "COMPLETED") "Hoàn tất" else "Đã duyệt")
        "FAILED" -> StatusConfig(Red400, Red700, "Thất bại")
        else -> StatusConfig(Slate300, Slate500, "Đã hủy")
    }

    var showCancelConfirm by remember { mutableStateOf(false) }

    GlassCard {
        Row {
            // Status bar
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
                    .background(statusConfig.barColor)
            )
            Column(modifier = Modifier.padding(12.dp).weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        formatCurrency(item.amount),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Slate800
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = statusConfig.barColor.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, statusConfig.barColor.copy(alpha = 0.3f))
                        ) {
                            Text(
                                "  ${statusConfig.label}  ",
                                modifier = Modifier.padding(vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                color = statusConfig.textColor
                            )
                        }
                        if (item.status == "PENDING" && !showCancelConfirm) {
                            Spacer(Modifier.width(8.dp))
                            TextButton(
                                onClick = { showCancelConfirm = true },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("Hủy", color = Red500, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item.createdAt?.let { dateStr ->
                        Text(formatDate(dateStr), style = MaterialTheme.typography.labelSmall, color = Slate400)
                        Text("·", color = Slate300)
                    }
                    Text(
                        "Nhận ${formatCurrency(item.netAmount ?: 0.0)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate500
                    )
                    Text("·", color = Slate300)
                    Text(
                        "Phí ${formatCurrency(item.fee ?: 0.0)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate500
                    )
                }

                // Cancel confirmation
                if (showCancelConfirm) {
                    Spacer(Modifier.height(8.dp))
                    Surface(
                        color = SkyBlue50,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Xác nhận hủy?", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall, color = Slate500)
                            TextButton(onClick = { showCancelConfirm = false }) {
                                Text("Không", style = MaterialTheme.typography.labelSmall)
                            }
                            TextButton(
                                onClick = { showCancelConfirm = false; onCancel() },
                                enabled = !isCancelling
                            ) {
                                if (isCancelling) {
                                    CircularProgressIndicator(Modifier.size(14.dp), strokeWidth = 2.dp)
                                } else {
                                    Text("Xác nhận", color = Red500, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class StatusConfig(
    val barColor: androidx.compose.ui.graphics.Color,
    val textColor: androidx.compose.ui.graphics.Color,
    val label: String
)

private fun formatCurrency(amount: Double): String {
    return String.format("%,d VND", amount.toLong())
}

private fun formatDate(dateStr: String): String {
    return try {
        val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val output = SimpleDateFormat("dd/MM/yyyy", Locale("vi"))
        output.format(input.parse(dateStr)!!)
    } catch (_: Exception) {
        try {
            val input = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val output = SimpleDateFormat("dd/MM/yyyy", Locale("vi"))
            output.format(input.parse(dateStr)!!)
        } catch (_: Exception) { dateStr }
    }
}
