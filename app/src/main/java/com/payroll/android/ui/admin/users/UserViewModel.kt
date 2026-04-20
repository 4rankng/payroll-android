package com.payroll.android.ui.admin.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payroll.android.data.remote.dto.*
import com.payroll.android.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserListState(
    val isLoading: Boolean = true,
    val summary: UserSummary? = null,
    val users: List<AdminUser> = emptyList(),
    val search: String = "",
    val roleFilter: String? = null,
    val page: Int = 1,
    val hasMore: Boolean = true,
    val isLoadingMore: Boolean = false,
    val showResetConfirm: Boolean = false,
    val resetTargetId: Int? = null,
    val error: String? = null
)

data class UserFormState(
    val isLoading: Boolean = false,
    val isEditing: Boolean = false,
    val username: String = "",
    val email: String = "",
    val fullname: String = "",
    val password: String = "",
    val role: String = "employee",
    val error: String? = null,
    val success: Boolean = false
)

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepo: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(UserListState())
    val state = _state.asStateFlow()

    private val _formState = MutableStateFlow(UserFormState())
    val formState = _formState.asStateFlow()

    init { loadAll() }

    fun loadAll() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            userRepo.getSummary().onSuccess { _state.value = _state.value.copy(summary = it) }
            loadUsers(reset = true)
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    private suspend fun loadUsers(reset: Boolean = false) {
        val s = _state.value
        val page = if (reset) 1 else s.page
        if (!reset) _state.value = s.copy(isLoadingMore = true)
        userRepo.getUsers(page, 20, s.search.ifBlank { null }, s.roleFilter)
            .onSuccess { result ->
                _state.value = _state.value.copy(
                    users = if (reset) result.items else s.users + result.items,
                    page = page + 1, hasMore = page < result.totalPages, isLoadingMore = false
                )
            }.onFailure { _state.value = _state.value.copy(isLoadingMore = false) }
    }

    fun onSearch(query: String) { _state.value = _state.value.copy(search = query); viewModelScope.launch { loadUsers(reset = true) } }
    fun onRoleFilter(role: String?) { _state.value = _state.value.copy(roleFilter = role); viewModelScope.launch { loadUsers(reset = true) } }
    fun loadMore() { viewModelScope.launch { loadUsers() } }

    fun suspendUser(id: Int) { viewModelScope.launch { userRepo.suspendUser(id).onSuccess { loadAll() } } }
    fun activateUser(id: Int) { viewModelScope.launch { userRepo.activateUser(id).onSuccess { loadAll() } } }

    fun showResetConfirm(id: Int) { _state.value = _state.value.copy(showResetConfirm = true, resetTargetId = id) }
    fun dismissResetConfirm() { _state.value = _state.value.copy(showResetConfirm = false, resetTargetId = null) }
    fun resetPassword() {
        val id = _state.value.resetTargetId ?: return
        viewModelScope.launch { userRepo.resetPassword(id).onSuccess { dismissResetConfirm() } }
    }

    private var editingUserId: Int = 0

    fun loadForm(userId: Int?) {
        if (userId == null) { _formState.value = UserFormState(); return }
        editingUserId = userId
        val user = _state.value.users.find { it.id == userId }
        if (user != null) {
            _formState.value = UserFormState(isEditing = true, username = user.username ?: "", email = user.email ?: "", fullname = user.fullname ?: "", role = user.role ?: "employee")
        }
    }

    fun onFormField(field: String, value: String) {
        _formState.value = when (field) {
            "username" -> _formState.value.copy(username = value)
            "email" -> _formState.value.copy(email = value)
            "fullname" -> _formState.value.copy(fullname = value)
            "password" -> _formState.value.copy(password = value)
            "role" -> _formState.value.copy(role = value)
            else -> _formState.value
        }
    }

    fun saveUser(onSuccess: () -> Unit) {
        val s = _formState.value
        if (s.username.isBlank() || (!s.isEditing && s.password.isBlank())) {
            _formState.value = s.copy(error = "Vui lòng nhập đầy đủ thông tin"); return
        }
        viewModelScope.launch {
            _formState.value = s.copy(isLoading = true, error = null)
            val result = if (s.isEditing) {
                userRepo.updateUser(editingUserId, UpdateUserRequest(username = s.username, email = s.email.ifBlank { null }, fullname = s.fullname.ifBlank { null }, password = s.password.ifBlank { null }, role = s.role))
            } else {
                userRepo.createUser(CreateUserRequest(username = s.username, email = s.email.ifBlank { null }, fullname = s.fullname.ifBlank { null }, password = s.password, role = s.role))
            }
            result.onSuccess { _formState.value = _formState.value.copy(isLoading = false, success = true); onSuccess() }
                .onFailure { _formState.value = _formState.value.copy(isLoading = false, error = it.message) }
        }
    }
}
