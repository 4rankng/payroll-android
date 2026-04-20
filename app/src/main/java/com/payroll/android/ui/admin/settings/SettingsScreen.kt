package com.payroll.android.ui.admin.settings

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
import com.payroll.android.data.remote.dto.Setting
import com.payroll.android.ui.components.*
import com.payroll.android.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) { viewModel.loadAll() }
    LaunchedEffect(state.successMessage) { state.successMessage?.let { snackbarHostState.showSnackbar(it); viewModel.clearSuccess() } }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(title = { Text("Cài đặt", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White),
                actions = {
                    IconButton(onClick = { viewModel.showNotificationForm() }) { Icon(Icons.Default.Notifications, null, tint = Sky500) }
                    IconButton(onClick = { viewModel.showEmailForm() }) { Icon(Icons.Default.Email, null, tint = Sky500) }
                })
        }, snackbarHost = { SnackbarHost(snackbarHostState) }, containerColor = Gray50
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item { SectionHeader("Cài đặt hệ thống") }
            items(state.settings, key = { it.id }) { setting ->
                GlassCard {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(setting.key ?: "", fontWeight = FontWeight.SemiBold, color = Gray800)
                            setting.description?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = Gray500) }
                            Text(setting.value ?: "", style = MaterialTheme.typography.bodySmall, color = Sky600)
                        }
                        IconButton(onClick = { viewModel.showEditDialog(setting) }) { Icon(Icons.Default.Edit, null, tint = Sky500, modifier = Modifier.size(18.dp)) }
                    }
                }
            }

            if (state.emailHistory.isNotEmpty()) {
                item { SectionHeader("Lịch sử email") }
                items(state.emailHistory, key = { it.id }) { email ->
                    GlassCard {
                        Text(email.subject ?: "", fontWeight = FontWeight.SemiBold, color = Gray800)
                        Text(email.recipients?.joinToString(", ") ?: "", style = MaterialTheme.typography.bodySmall, color = Gray500)
                        StatusBadge(email.status ?: "sent")
                    }
                }
            }
        }
    }

    // Edit setting dialog
    if (state.showEditDialog && state.editingSetting != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissEditDialog() },
            title = { Text("Sửa: ${state.editingSetting?.key}") },
            text = { OutlinedTextField(value = state.editValue, onValueChange = { viewModel.onEditValueChange(it) }, label = { Text("Giá trị") }, modifier = Modifier.fillMaxWidth()) },
            confirmButton = { TextButton(onClick = { viewModel.saveSetting() }) { Text("Lưu") } },
            dismissButton = { TextButton(onClick = { viewModel.dismissEditDialog() }) { Text("Hủy") } }
        )
    }

    // Notification form
    if (state.showNotificationForm) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissNotificationForm() },
            title = { Text("Gửi thông báo") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = state.notifTitle, onValueChange = { viewModel.onNotifField("title", it) }, label = { Text("Tiêu đề *") })
                    OutlinedTextField(value = state.notifMessage, onValueChange = { viewModel.onNotifField("message", it) }, label = { Text("Nội dung *") })
                    OutlinedTextField(value = state.notifUserId, onValueChange = { viewModel.onNotifField("userId", it) }, label = { Text("ID người nhận (để trống = tất cả)") })
                }
            },
            confirmButton = { TextButton(onClick = { viewModel.sendNotification() }) { Text("Gửi") } },
            dismissButton = { TextButton(onClick = { viewModel.dismissNotificationForm() }) { Text("Hủy") } }
        )
    }

    // Email form
    if (state.showEmailForm) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissEmailForm() },
            title = { Text("Gửi email") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = state.emailRecipients, onValueChange = { viewModel.onEmailField("recipients", it) }, label = { Text("Người nhận (phẩy)") })
                    OutlinedTextField(value = state.emailSubject, onValueChange = { viewModel.onEmailField("subject", it) }, label = { Text("Tiêu đề *") })
                    OutlinedTextField(value = state.emailBody, onValueChange = { viewModel.onEmailField("body", it) }, label = { Text("Nội dung *") }, minLines = 3)
                }
            },
            confirmButton = { TextButton(onClick = { viewModel.sendEmail() }) { Text("Gửi") } },
            dismissButton = { TextButton(onClick = { viewModel.dismissEmailForm() }) { Text("Hủy") } }
        )
    }
}
