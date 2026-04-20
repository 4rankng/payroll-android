package com.payroll.android.ui.flexiblepay

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payroll.android.data.remote.dto.*
import com.payroll.android.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FlexiblePayState(
    val profile: EmployeeProfile? = null,
    val advanceInfo: AdvancePaymentInfo? = null,
    val history: List<AdvancePaymentHistoryItem> = emptyList(),
    val feeCalculation: FeeCalculationResponse? = null,
    val isLoading: Boolean = true,
    val isLoadingHistory: Boolean = false,
    val isSubmitting: Boolean = false,
    val requestAmount: String = "",
    val sliderAmount: Float = 0f,
    val error: String? = null,
    val successMessage: String? = null,
    val unreadCount: Int = 0,
    val notifications: List<NotificationItem> = emptyList(),
    val showNotificationSheet: Boolean = false,
    val showPasswordSheet: Boolean = false,
    val passwordError: String? = null,
    val passwordSuccess: Boolean = false,
    val isChangingPassword: Boolean = false,
    val historyPage: Int = 1,
    val hasMoreHistory: Boolean = true,
    val cancellingId: Int? = null
)

@HiltViewModel
class FlexiblePayViewModel @Inject constructor(
    private val advanceRepo: AdvancePaymentRepository,
    private val employeeRepo: EmployeeRepository,
    private val authRepo: AuthRepository,
    private val notifRepo: NotificationRepository
) : ViewModel() {

    private val _state = MutableStateFlow(FlexiblePayState())
    val state = _state.asStateFlow()

    init { loadAll() }

    private fun loadAll() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            employeeRepo.getProfile().onSuccess { _state.value = _state.value.copy(profile = it) }
            advanceRepo.getInfo().onSuccess { _state.value = _state.value.copy(advanceInfo = it) }
            notifRepo.getUnreadCount().onSuccess { _state.value = _state.value.copy(unreadCount = it) }
            loadHistory(reset = true)
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    private suspend fun loadHistory(reset: Boolean = false) {
        val s = _state.value
        if (!reset && !s.hasMoreHistory) return
        val page = if (reset) 1 else s.historyPage
        _state.value = if (reset) s.copy(isLoadingHistory = true) else s
        advanceRepo.getHistory(page, 10).onSuccess { result ->
            _state.value = _state.value.copy(
                history = if (reset) result.items else s.history + result.items,
                historyPage = page + 1,
                hasMoreHistory = page < result.totalPages,
                isLoadingHistory = false
            )
        }.onFailure { _state.value = _state.value.copy(isLoadingHistory = false) }
    }

    fun onAmountChange(amount: String) {
        _state.value = _state.value.copy(requestAmount = amount, feeCalculation = null)
        val d = amount.toDoubleOrNull()
        if (d != null && d > 0) calculateFee(d)
    }

    fun onSliderChange(value: Float) {
        val info = _state.value.advanceInfo ?: return
        val amount = (value * info.remainingAmount).toLong().toDouble()
        _state.value = _state.value.copy(
            sliderAmount = value,
            requestAmount = String.format("%.0f", amount),
            feeCalculation = null
        )
        calculateFee(amount)
    }

    private fun calculateFee(amount: Double) {
        viewModelScope.launch {
            advanceRepo.calculateFee(amount).onSuccess {
                _state.value = _state.value.copy(feeCalculation = it)
            }
        }
    }

    fun submitRequest() {
        val amount = _state.value.requestAmount.toDoubleOrNull() ?: return
        viewModelScope.launch {
            _state.value = _state.value.copy(isSubmitting = true, error = null)
            advanceRepo.requestAdvance(amount)
                .onSuccess {
                    _state.value = _state.value.copy(
                        isSubmitting = false,
                        successMessage = "Gửi yêu cầu thành công!",
                        requestAmount = "",
                        sliderAmount = 0f,
                        feeCalculation = null
                    )
                    // Refresh info and history
                    advanceRepo.getInfo().onSuccess { _state.value = _state.value.copy(advanceInfo = it) }
                    loadHistory(reset = true)
                }
                .onFailure {
                    _state.value = _state.value.copy(isSubmitting = false, error = it.message)
                }
        }
    }

    fun cancelRequest(id: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(cancellingId = id)
            advanceRepo.cancelRequest(id)
                .onSuccess {
                    _state.value = _state.value.copy(cancellingId = null)
                    advanceRepo.getInfo().onSuccess { _state.value = _state.value.copy(advanceInfo = it) }
                    loadHistory(reset = true)
                }
                .onFailure { _state.value = _state.value.copy(cancellingId = null) }
        }
    }

    fun clearSuccess() { _state.value = _state.value.copy(successMessage = null) }
    fun clearError() { _state.value = _state.value.copy(error = null) }

    fun loadMoreHistory() { viewModelScope.launch { loadHistory() } }

    fun showNotificationSheet(show: Boolean) {
        _state.value = _state.value.copy(showNotificationSheet = show)
        if (show) {
            viewModelScope.launch {
                notifRepo.getNotifications(1, 20).onSuccess { _state.value = _state.value.copy(notifications = it.items) }
            }
        }
    }

    fun showPasswordSheet(show: Boolean) { _state.value = _state.value.copy(showPasswordSheet = show, passwordError = null, passwordSuccess = false) }

    fun markNotificationRead(id: Int) {
        viewModelScope.launch {
            notifRepo.markRead(id).onSuccess {
                _state.value = _state.value.copy(
                    notifications = _state.value.notifications.map { if (it.id == id) it.copy(read = true) else it },
                    unreadCount = maxOf(0, _state.value.unreadCount - 1)
                )
            }
        }
    }

    fun markAllRead() {
        viewModelScope.launch {
            notifRepo.markAllRead().onSuccess {
                _state.value = _state.value.copy(
                    notifications = _state.value.notifications.map { it.copy(read = true) },
                    unreadCount = 0
                )
            }
        }
    }

    fun changePassword(current: String, new: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isChangingPassword = true, passwordError = null)
            employeeRepo.changePassword(current, new)
                .onSuccess { _state.value = _state.value.copy(isChangingPassword = false, passwordSuccess = true) }
                .onFailure { _state.value = _state.value.copy(isChangingPassword = false, passwordError = it.message) }
        }
    }

    fun logout() { viewModelScope.launch { authRepo.logout() } }

    fun getGreeting(): String {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        return when {
            hour < 12 -> "Chào buổi sáng"
            hour < 18 -> "Chào buổi chiều"
            else -> "Chào buổi tối"
        }
    }
}
