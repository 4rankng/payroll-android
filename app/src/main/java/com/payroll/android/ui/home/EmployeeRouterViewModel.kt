package com.payroll.android.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payroll.android.data.remote.dto.EmployeeProfile
import com.payroll.android.domain.repository.AuthRepository
import com.payroll.android.domain.repository.EmployeeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RouterState(
    val isLoading: Boolean = true,
    val profile: EmployeeProfile? = null,
    val error: String? = null,
    val isLoggedOut: Boolean = false
)

@HiltViewModel
class EmployeeRouterViewModel @Inject constructor(
    private val employeeRepo: EmployeeRepository,
    private val authRepo: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RouterState())
    val state = _state.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            employeeRepo.getProfile()
                .onSuccess { profile ->
                    _state.value = RouterState(isLoading = false, profile = profile)
                }
                .onFailure {
                    _state.value = RouterState(isLoading = false, error = it.message)
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepo.logout()
            _state.value = _state.value.copy(isLoggedOut = true)
        }
    }
}
