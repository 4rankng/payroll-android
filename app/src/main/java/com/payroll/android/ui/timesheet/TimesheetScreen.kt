package com.payroll.android.ui.timesheet

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.payroll.android.data.remote.dto.TimesheetEntry
import com.payroll.android.ui.components.*
import com.payroll.android.ui.theme.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimesheetScreen(
    viewModel: TimesheetViewModel,
    onLogout: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var showMenu by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.refreshUnread() }

    // Notifications & Password sheets
    if (state.showNotificationSheet) {
        NotificationSheet(
            notifications = state.notifications,
            unreadCount = state.unreadCount,
            isLoading = false,
            onDismiss = { viewModel.showNotificationSheet(false) },
            onMarkRead = viewModel::markNotificationRead,
            onMarkAllRead = viewModel.markAllRead
        )
    }
    if (state.showPasswordSheet) {
        PasswordSheet(
            onDismiss = { viewModel.showPasswordSheet(false) },
            onSubmit = { cur, new -> viewModel.changePassword(cur, new) },
            isLoading = state.isChangingPassword,
            error = state.passwordError,
            onSuccess = state.passwordSuccess
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(Gray50)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            HeaderSection(
                profile = state.profile,
                greeting = viewModel.getGreeting(),
                unreadCount = state.unreadCount,
                showMenu = showMenu,
                onMenuToggle = { showMenu = !showMenu },
                onMenuDismiss = { showMenu = false },
                onChangePassword = { showMenu = false; viewModel.showPasswordSheet(true) },
                onNotifications = { viewModel.loadNotifications(); viewModel.showNotificationSheet(true) },
                onLogout = { viewModel.logout(); onLogout() }
            )

            if (state.isLoading) {
                Column(modifier = Modifier.padding(16.dp)) { LoadingList(4) }
            } else {
                // Month selector
                MonthSelector(
                    selectedMonth = state.selectedMonth,
                    onSelect = viewModel::selectMonth
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
                ) {
                    // Summary cards
                    item {
                        SummaryCards(
                            summary = state.summary,
                            payableLimitPercentage = state.payableLimitPercentage
                        )
                    }

                    // Timesheet entries grouped by date
                    val grouped = state.timesheetEntries.groupBy { it.date }
                    items(entries = grouped.entries.toList(), key = { it.key }) { (date, entries) ->
                        DayCard(date = date, entries = entries)
                    }

                    // Load more
                    if (state.hasMore) {
                        item {
                            LaunchedEffect(Unit) { viewModel.loadTimesheet() }
                            Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Sky500, strokeWidth = 2.dp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderSection(
    profile: com.payroll.android.data.remote.dto.EmployeeProfile?,
    greeting: String,
    unreadCount: Int,
    showMenu: Boolean,
    onMenuToggle: () -> Unit,
    onMenuDismiss: () -> Unit,
    onChangePassword: () -> Unit,
    onNotifications: () -> Unit,
    onLogout: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(Sky400, Sky600)))
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AvatarInitials(name = profile?.fullname, modifier = Modifier.size(44.dp))
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(greeting, color = White.copy(alpha = 0.8f), fontSize = 13.sp)
            Text(
                profile?.fullname ?: "Nhân viên",
                color = White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
        }

        // Notification bell
        BadgedBox(
            badge = {
                if (unreadCount > 0) {
                    Badge(containerColor = Red500) { Text("$unreadCount", color = White, fontSize = 10.sp) }
                }
            }
        ) {
            IconButton(onClick = onNotifications) {
                Icon(Icons.Default.Notifications, "Thông báo", tint = White)
            }
        }

        // Profile menu
        Box {
            IconButton(onClick = onMenuToggle) {
                Icon(Icons.Default.MoreVert, null, tint = White)
            }
            DropdownMenu(expanded = showMenu, onDismissRequest = onMenuDismiss) {
                DropdownMenuItem(
                    text = { Text("Đổi mật khẩu") },
                    onClick = onChangePassword,
                    leadingIcon = { Icon(Icons.Default.Lock, null) }
                )
                DropdownMenuItem(
                    text = { Text("Đăng xuất") },
                    onClick = onLogout,
                    leadingIcon = { Icon(Icons.Default.Logout, null) }
                )
            }
        }
    }
}

@Composable
private fun MonthSelector(
    selectedMonth: YearMonth,
    onSelect: (YearMonth) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val months = (0..5).map { YearMonth.now().minusMonths(it.toLong()) }
    val vietnameseLocale = Locale("vi", "VN")
    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", vietnameseLocale)

    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            OutlinedButton(onClick = { expanded = true }) {
                Text(selectedMonth.format(formatter).replaceFirstChar { it.uppercase() })
                Spacer(Modifier.width(4.dp))
                Icon(Icons.Default.ArrowDropDown, null)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                months.forEach { m ->
                    DropdownMenuItem(
                        text = { Text(m.format(formatter).replaceFirstChar { it.uppercase() }) },
                        onClick = { expanded = false; onSelect(m) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryCards(
    summary: com.payroll.android.data.remote.dto.SummaryResponse?,
    payableLimitPercentage: Double?
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SummaryCard(
                title = "Tổng lương",
                value = formatVnd(summary?.totalSalary ?: 0.0),
                subtitle = summary?.month ?: "",
                modifier = Modifier.weight(1f)
            )
            SummaryCard(
                title = "Tổng công",
                value = "${summary?.totalHours ?: 0.0}h",
                subtitle = "${summary?.totalDays ?: 0} ngày làm việc",
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SummaryCard(
                title = "Hạn mức trả",
                value = if (payableLimitPercentage != null) {
                    val limit = (summary?.totalSalary ?: 0.0) * payableLimitPercentage / 100.0
                    formatVnd(limit)
                } else formatVnd(summary?.payableLimit ?: 0.0),
                subtitle = if (payableLimitPercentage != null) "${payableLimitPercentage}%" else "",
                modifier = Modifier.weight(1f)
            )
            SummaryCard(
                title = "Đã nhận",
                value = formatVnd(summary?.amountReceived ?: 0.0),
                subtitle = "",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier) {
        Text(title, style = MaterialTheme.typography.labelSmall, color = Gray500)
        Spacer(Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Sky700)
        if (subtitle.isNotBlank()) {
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = Gray400)
        }
    }
}

@Composable
private fun DayCard(
    date: String,
    entries: List<TimesheetEntry>
) {
    val vietnameseLocale = Locale("vi", "VN")
    val displayDate = try {
        val ld = LocalDate.parse(date)
        ld.format(DateTimeFormatter.ofPattern("EEEE, dd/MM", vietnameseLocale))
            .replaceFirstChar { it.uppercase() }
    } catch (e: Exception) { date }

    GlassCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(displayDate, fontWeight = FontWeight.SemiBold, color = Gray800)
            // Payment status of first entry
            val status = entries.firstOrNull()?.paymentStatus
            if (status != null) {
                val (text, color) = when (status.lowercase()) {
                    "paid" -> "Đã trả" to Green500
                    else -> "Chưa trả" to Amber500
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = color.copy(alpha = 0.15f)
                ) {
                    Text(
                        text,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        color = color,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        entries.forEach { entry ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${entry.hoursWorked ?: 0.0}h", style = MaterialTheme.typography.bodyMedium, color = Gray600, modifier = Modifier.weight(1f))
                Text(formatVnd(entry.totalSalary ?: 0.0), style = MaterialTheme.typography.bodyMedium, color = Gray600, modifier = Modifier.weight(1f))
                Text(formatVnd(entry.amountReceived ?: 0.0), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = Sky600, modifier = Modifier.weight(1f))
            }
        }
        Spacer(Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Giờ làm", style = MaterialTheme.typography.labelSmall, color = Gray400, modifier = Modifier.weight(1f))
            Text("Tổng lương", style = MaterialTheme.typography.labelSmall, color = Gray400, modifier = Modifier.weight(1f))
            Text("Đã nhận", style = MaterialTheme.typography.labelSmall, color = Gray400, modifier = Modifier.weight(1f))
        }
    }
}

private fun formatVnd(amount: Double): String {
    return String.format("%,.0fđ", amount)
}
