package com.payroll.android.ui.role

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payroll.android.data.local.TokenManager
import com.payroll.android.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RoleRouterState(
    val isLoading: Boolean = true,
    val role: String? = null,
    val userName: String? = null,
    val error: String? = null
)

@HiltViewModel
class RoleRouterViewModel @Inject constructor(
    private val authRepo: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow(RoleRouterState())
    val state = _state.asStateFlow()

    init {
        determineRole()
    }

    private fun determineRole() {
        viewModelScope.launch {
            val role = tokenManager.getRole().firstOrNull()
            val name = tokenManager.getFullName().firstOrNull()
            if (role != null) {
                _state.value = RoleRouterState(isLoading = false, role = role, userName = name)
            } else {
                _state.value = RoleRouterState(isLoading = false, error = "Phiên hết hạn")
            }
        }
    }

    fun logout() {
        viewModelScope.launch { authRepo.logout() }
    }
}
