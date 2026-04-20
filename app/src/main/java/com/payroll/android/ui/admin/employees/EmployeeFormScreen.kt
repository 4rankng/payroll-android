package com.payroll.android.ui.admin.employees

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.payroll.android.data.remote.dto.AdminEmployee
import com.payroll.android.data.remote.dto.CreateEmployeeRequest
import com.payroll.android.data.remote.dto.UpdateEmployeeRequest
import com.payroll.android.domain.repository.AdminEmployeeRepository
import com.payroll.android.ui.theme.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EmployeeFormState(
    val isLoading: Boolean = false,
    val isEditing: Boolean = false,
    val fullname: String = "",
    val email: String = "",
    val cccd: String = "",
    val address: String = "",
    val mobile: String = "",
    val bank: String = "",
    val bankAccountNumber: String = "",
    val bankAccountName: String = "",
    val dateOfBirth: String = "",
    val error: String? = null,
    val success: Boolean = false
)

@HiltViewModel
class EmployeeFormViewModel @Inject constructor(
    private val employeeRepo: AdminEmployeeRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EmployeeFormState())
    val state = _state.asStateFlow()

    fun onFieldChange(field: String, value: String) {
        _state.value = when (field) {
            "fullname" -> _state.value.copy(fullname = value)
            "email" -> _state.value.copy(email = value)
            "cccd" -> _state.value.copy(cccd = value)
            "address" -> _state.value.copy(address = value)
            "mobile" -> _state.value.copy(mobile = value)
            "bank" -> _state.value.copy(bank = value)
            "bankAccountNumber" -> _state.value.copy(bankAccountNumber = value)
            "bankAccountName" -> _state.value.copy(bankAccountName = value)
            "dateOfBirth" -> _state.value.copy(dateOfBirth = value)
            else -> _state.value
        }
    }

    private var editingId: Int = 0

    fun loadEmployee(id: Int) {
        editingId = id
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            employeeRepo.getEmployee(id).onSuccess { emp ->
                _state.value = _state.value.copy(
                    isLoading = false, isEditing = true,
                    fullname = emp.fullname ?: "", email = emp.email ?: "",
                    cccd = emp.cccd ?: "", address = emp.address ?: "",
                    mobile = emp.mobile ?: "", bank = emp.bank ?: "",
                    bankAccountNumber = emp.bankAccountNumber ?: "",
                    bankAccountName = emp.bankAccountName ?: "",
                    dateOfBirth = emp.dateOfBirth ?: ""
                )
            }.onFailure { _state.value = _state.value.copy(isLoading = false, error = it.message) }
        }
    }

    fun save(onSuccess: () -> Unit) {
        val s = _state.value
        if (s.fullname.isBlank()) {
            _state.value = s.copy(error = "Vui lòng nhập họ tên")
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            if (s.isEditing) {
                employeeRepo.updateEmployee(editingId, UpdateEmployeeRequest(
                    fullname = s.fullname, email = s.email.ifBlank { null },
                    cccd = s.cccd.ifBlank { null }, address = s.address.ifBlank { null },
                    mobile = s.mobile.ifBlank { null }, bank = s.bank.ifBlank { null },
                    bankAccountNumber = s.bankAccountNumber.ifBlank { null },
                    bankAccountName = s.bankAccountName.ifBlank { null },
                    dateOfBirth = s.dateOfBirth.ifBlank { null }
                ))
            } else {
                employeeRepo.createEmployee(CreateEmployeeRequest(
                    fullname = s.fullname, email = s.email.ifBlank { null },
                    cccd = s.cccd.ifBlank { null }, address = s.address.ifBlank { null },
                    mobile = s.mobile.ifBlank { null }, bank = s.bank.ifBlank { null },
                    bankAccountNumber = s.bankAccountNumber.ifBlank { null },
                    bankAccountName = s.bankAccountName.ifBlank { null },
                    dateOfBirth = s.dateOfBirth.ifBlank { null }
                ))
            }.onSuccess { _state.value = _state.value.copy(isLoading = false, success = true); onSuccess() }
                .onFailure { _state.value = _state.value.copy(isLoading = false, error = it.message) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeFormScreen(
    employeeId: Int?,
    viewModel: EmployeeFormViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(employeeId) {
        if (employeeId != null) viewModel.loadEmployee(employeeId)
    }

    LaunchedEffect(state.success) {
        if (state.success) onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditing) "Sửa nhân viên" else "Thêm nhân viên", fontWeight = FontWeight.Bold) },
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
            if (state.error != null) {
                Text(state.error!!, color = Red500, style = MaterialTheme.typography.bodySmall)
            }
            FormField("Họ tên *", state.fullname) { viewModel.onFieldChange("fullname", it) }
            FormField("Email", state.email, KeyboardType.Email) { viewModel.onFieldChange("email", it) }
            FormField("Số CCCD", state.cccd) { viewModel.onFieldChange("cccd", it) }
            FormField("Địa chỉ", state.address) { viewModel.onFieldChange("address", it) }
            FormField("Số điện thoại", state.mobile, KeyboardType.Phone) { viewModel.onFieldChange("mobile", it) }
            FormField("Ngân hàng", state.bank) { viewModel.onFieldChange("bank", it) }
            FormField("Số tài khoản", state.bankAccountNumber) { viewModel.onFieldChange("bankAccountNumber", it) }
            FormField("Tên tài khoản", state.bankAccountName) { viewModel.onFieldChange("bankAccountName", it) }
            FormField("Ngày sinh (YYYY-MM-DD)", state.dateOfBirth) { viewModel.onFieldChange("dateOfBirth", it) }

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { viewModel.save(onBack) },
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Sky500)
            ) {
                if (state.isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = White, strokeWidth = 2.dp)
                else Text(if (state.isEditing) "Cập nhật" else "Thêm nhân viên", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun FormField(
    label: String,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
    )
}
