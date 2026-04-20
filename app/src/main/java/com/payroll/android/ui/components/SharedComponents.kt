package com.payroll.android.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.payroll.android.ui.theme.*

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Tìm kiếm...",
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(placeholder, color = Gray400) },
        leadingIcon = { Icon(Icons.Default.Search, null, tint = Gray400) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Close, null, tint = Gray400)
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Sky400,
            unfocusedBorderColor = Gray200,
            focusedContainerColor = White,
            unfocusedContainerColor = White
        )
    )
}

@Composable
fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (selected) Sky500 else Gray100,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = if (selected) White else Gray600,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun FilterBar(
    filters: List<Pair<String, Boolean>>,
    onFilterClick: (Int) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.horizontalScroll(rememberScrollState())
    ) {
        filters.forEachIndexed { index, (label, selected) ->
            FilterChip(label = label, selected = selected) { onFilterClick(index) }
        }
    }
}

private fun rememberScrollState() = androidx.compose.foundation.rememberScrollState()

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (color, text) = when (status.lowercase()) {
        "active", "approved", "paid", "completed", "disbursed" -> Emerald500 to "Hoạt động"
        "pending", "waiting" -> Amber500 to "Chờ duyệt"
        "rejected", "cancelled", "suspended" -> Red500 to "Từ chối"
        "paused" -> Gray400 to "Tạm dừng"
        "completed", "done" -> Sky600 to "Hoàn thành"
        "working" -> Emerald500 to "Đang làm"
        "unassigned" -> Gray400 to "Chưa phân công"
        else -> Gray400 to status
    }
    val (badgeColor, label) = when (status.lowercase()) {
        "active" -> Emerald500 to "Hoạt động"
        "approved" -> Emerald500 to "Đã duyệt"
        "paid" -> Emerald500 to "Đã trả"
        "completed" -> Sky600 to "Hoàn thành"
        "pending" -> Amber500 to "Chờ duyệt"
        "waiting" -> Amber500 to "Đang chờ"
        "rejected" -> Red500 to "Từ chối"
        "cancelled" -> Red500 to "Đã hủy"
        "suspended" -> Red500 to "Đình chỉ"
        "paused" -> Gray400 to "Tạm dừng"
        "working" -> Emerald500 to "Đang làm"
        "unassigned" -> Gray400 to "Chưa phân công"
        "disbursed" -> Sky600 to "Đã giải ngân"
        else -> Gray400 to status
    }
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = badgeColor.copy(alpha = 0.15f),
        modifier = modifier
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            color = badgeColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun LoadMoreButton(
    onClick: () -> Unit,
    isLoading: Boolean
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Sky500)
        } else {
            Text("Xem thêm", color = Sky500)
        }
    }
}

@Composable
fun EmptyState(
    icon: androidx.compose.ui.graphics.vector.ImageVector = Icons.Default.Search,
    message: String = "Không có dữ liệu"
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, null, modifier = Modifier.size(48.dp), tint = Gray300)
        Spacer(Modifier.height(12.dp))
        Text(message, color = Gray400, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmLabel: String = "Xác nhận",
    cancelLabel: String = "Hủy",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isDestructive: Boolean = false
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmLabel, color = if (isDestructive) Red500 else Sky500)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(cancelLabel) }
        }
    )
}

@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    color: androidx.compose.ui.graphics.Color = Sky600
) {
    GlassCard(modifier = modifier) {
        Text(title, style = MaterialTheme.typography.labelSmall, color = Gray500)
        Spacer(Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
        if (subtitle != null) {
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = Gray400)
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    action: String? = null,
    onAction: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Gray800)
        if (action != null && onAction != null) {
            TextButton(onClick = onAction) { Text(action, color = Sky500) }
        }
    }
}

@Composable
fun BulkActionBar(
    selectedCount: Int,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onClear: () -> Unit
) {
    Surface(
        color = Sky600,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Đã chọn $selectedCount", color = White, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            TextButton(onClick = onApprove) { Text("Duyệt", color = White) }
            Spacer(Modifier.width(8.dp))
            TextButton(onClick = onReject) { Text("Từ chối", color = Amber400) }
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = onClear) { Icon(Icons.Default.Close, null, tint = White) }
        }
    }
}
