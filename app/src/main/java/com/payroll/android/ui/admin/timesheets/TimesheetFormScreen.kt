package com.payroll.android.ui.admin.timesheets

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
fun TimesheetFormScreen(
    timesheetId: Int?,
    viewModel: AdminTimesheetViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.formState.collectAsState()

    LaunchedEffect(timesheetId) { viewModel.loadForm(timesheetId) }
    LaunchedEffect(state.success) { if (state.success) onBack() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditing) "Sửa chấm công" else "Thêm chấm công", fontWeight = FontWeight.Bold) },
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
            Field("ID Nhân viên *", state.employeeId) { viewModel.onFormField("employeeId", it) }
            Field("ID Dự án *", state.projectId) { viewModel.onFormField("projectId", it) }
            Field("Ngày (YYYY-MM-DD) *", state.date) { viewModel.onFormField("date", it) }
            Field("Số giờ làm *", state.hoursWorked) { viewModel.onFormField("hoursWorked", it) }

            Text("Loại công", fontWeight = FontWeight.SemiBold, color = Gray600, style = MaterialTheme.typography.bodySmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("regular" to "Bình thường", "overtime" to "Tăng ca", "holiday" to "Ngày lễ").forEach { (key, label) ->
                    Surface(
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                        color = if (state.paytype == key) Sky500 else Gray100
                    ) {
                        TextButton(onClick = { viewModel.onFormField("paytype", key) }) {
                            Text(label, color = if (state.paytype == key) White else Gray600)
                        }
                    }
                }
            }

            Field("Mức lương/giờ", state.payrate) { viewModel.onFormField("payrate", it) }
            Field("Ghi chú", state.note) { viewModel.onFormField("note", it) }

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { viewModel.saveTimesheet(onBack) },
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Sky500)
            ) {
                if (state.isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = White, strokeWidth = 2.dp)
                else Text(if (state.isEditing) "Cập nhật" else "Tạo chấm công", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun Field(label: String, value: String, onChange: (String) -> Unit) {
    OutlinedTextField(value = value, onValueChange = onChange, label = { Text(label) }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
}
