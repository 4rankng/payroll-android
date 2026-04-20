package com.payroll.android.ui.admin.projects

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.payroll.android.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectFormScreen(
    projectId: Int?,
    viewModel: ProjectViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.formState.collectAsState()

    LaunchedEffect(projectId) { viewModel.loadForm(projectId) }
    LaunchedEffect(state.success) { if (state.success) onBack() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditing) "Sửa dự án" else "Thêm dự án", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
            )
        },
        containerColor = Gray50
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (state.error != null) Text(state.error!!, color = Red500, style = MaterialTheme.typography.bodySmall)

            FormField("Tên dự án *", state.name) { viewModel.onFormField("name", it) }
            FormField("Mã dự án", state.code) { viewModel.onFormField("code", it) }
            FormField("Khách hàng", state.clientName) { viewModel.onFormField("clientName", it) }
            FormField("Mô tả", state.description) { viewModel.onFormField("description", it) }
            FormField("Ngày bắt đầu (YYYY-MM-DD)", state.startDate) { viewModel.onFormField("startDate", it) }
            FormField("Ngày kết thúc (YYYY-MM-DD)", state.endDate) { viewModel.onFormField("endDate", it) }

            // Status selector
            Text("Trạng thái", fontWeight = FontWeight.SemiBold, color = Gray600, style = MaterialTheme.typography.bodySmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("active", "paused", "completed").forEach { status ->
                    FilterChip(
                        label = when (status) { "active" -> "Hoạt động"; "paused" -> "Tạm dừng"; else -> "Hoàn thành" },
                        selected = state.status == status
                    ) { viewModel.onFormField("status", status) }
                }
            }

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { viewModel.saveProject(onBack) },
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Sky500)
            ) {
                if (state.isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = White, strokeWidth = 2.dp)
                else Text(if (state.isEditing) "Cập nhật" else "Tạo dự án", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun FormField(label: String, value: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value, onValueChange = onChange,
        label = { Text(label) }, singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
    )
}

@Composable
private fun FilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
        color = if (selected) Sky500 else Gray100
    ) {
        TextButton(onClick = onClick) {
            Text(label, color = if (selected) White else Gray600, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal)
        }
    }
}
