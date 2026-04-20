package com.payroll.android.ui.admin.users

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
import com.payroll.android.ui.components.*
import com.payroll.android.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(
    viewModel: UserViewModel,
    onAddUser: () -> Unit,
    onEditUser: (Int) -> Unit
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) { viewModel.loadAll() }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Người dùng", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White),
                actions = { IconButton(onClick = onAddUser) { Icon(Icons.Default.Add, null, tint = Sky500) } })
        }, containerColor = Gray50
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            state.summary?.let { s ->
                Row(Modifier.padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard("Tổng", "${s.totalUsers}", Modifier.weight(1f))
                    StatCard("Hoạt động", "${s.activeUsers}", Modifier.weight(1f), color = Emerald500)
                    StatCard("Đình chỉ", "${s.suspendedUsers}", Modifier.weight(1f), color = Red500)
                }
            }
            SearchBar(state.search, viewModel::onSearch, "Tìm người dùng...", Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
            Row(Modifier.padding(horizontal = 16.dp, vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip("Tất cả", state.roleFilter == null) { viewModel.onRoleFilter(null) }
                FilterChip("Admin", state.roleFilter == "admin") { viewModel.onRoleFilter("admin") }
                FilterChip("Partner", state.roleFilter == "partner") { viewModel.onRoleFilter("partner") }
                FilterChip("Employee", state.roleFilter == "employee") { viewModel.onRoleFilter("employee") }
            }

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Sky500) }
            } else {
                LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.users, key = { it.id }) { user ->
                        GlassCard {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text(user.fullname ?: user.username ?: "", fontWeight = FontWeight.SemiBold, color = Gray800)
                                    Text("@${user.username} · ${user.email ?: ""}", style = MaterialTheme.typography.bodySmall, color = Gray500)
                                    Surface(shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp), color = Sky100) {
                                        Text(user.role ?: "", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = Sky700)
                                    }
                                }
                                Column {
                                    Row {
                                        IconButton(onClick = { onEditUser(user.id) }) { Icon(Icons.Default.Edit, null, tint = Sky500, modifier = Modifier.size(18.dp)) }
                                        if (user.status == "active") {
                                            IconButton(onClick = { viewModel.suspendUser(user.id) }) { Icon(Icons.Default.Block, null, tint = Amber500, modifier = Modifier.size(18.dp)) }
                                        } else {
                                            IconButton(onClick = { viewModel.activateUser(user.id) }) { Icon(Icons.Default.CheckCircle, null, tint = Emerald500, modifier = Modifier.size(18.dp)) }
                                        }
                                        IconButton(onClick = { viewModel.showResetConfirm(user.id) }) { Icon(Icons.Default.Key, null, tint = Gray400, modifier = Modifier.size(18.dp)) }
                                    }
                                    StatusBadge(user.status ?: "active")
                                }
                            }
                        }
                    }
                    if (state.hasMore) item { LoadMoreButton(viewModel::loadMore, state.isLoadingMore) }
                }
            }
        }
    }

    if (state.showResetConfirm) {
        ConfirmDialog("Đặt lại mật khẩu", "Đặt lại mật khẩu cho người dùng này?", "Đặt lại", onConfirm = { viewModel.resetPassword() }, onDismiss = { viewModel.dismissResetConfirm() })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserFormScreen(
    userId: Int?,
    viewModel: UserViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.formState.collectAsState()
    LaunchedEffect(userId) { viewModel.loadForm(userId) }
    LaunchedEffect(state.success) { if (state.success) onBack() }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(if (state.isEditing) "Sửa người dùng" else "Thêm người dùng", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White))
        }, containerColor = Gray50
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (state.error != null) Text(state.error!!, color = Red500, style = MaterialTheme.typography.bodySmall)
            Field("Tên đăng nhập *", state.username) { viewModel.onFormField("username", it) }
            Field("Họ tên", state.fullname) { viewModel.onFormField("fullname", it) }
            Field("Email", state.email) { viewModel.onFormField("email", it) }
            if (!state.isEditing) Field("Mật khẩu *", state.password) { viewModel.onFormField("password", it) }
            else Field("Mật khẩu mới (để trống nếu không đổi)", state.password) { viewModel.onFormField("password", it) }

            Text("Vai trò", fontWeight = FontWeight.SemiBold, color = Gray600, style = MaterialTheme.typography.bodySmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("admin" to "Admin", "partner" to "Partner", "employee" to "Employee").forEach { (key, label) ->
                    Surface(shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp), color = if (state.role == key) Sky500 else Gray100) {
                        TextButton(onClick = { viewModel.onFormField("role", key) }) { Text(label, color = if (state.role == key) White else Gray600) }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Button(onClick = { viewModel.saveUser(onBack) }, enabled = !state.isLoading, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = Sky500)) {
                if (state.isLoading) CircularProgressIndicator(Modifier.size(20.dp), color = White, strokeWidth = 2.dp)
                else Text(if (state.isEditing) "Cập nhật" else "Tạo người dùng", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun Field(label: String, value: String, onChange: (String) -> Unit) { OutlinedTextField(value = value, onValueChange = onChange, label = { Text(label) }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)) }
