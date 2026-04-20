package com.payroll.android.ui.timesheet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payroll.android.data.remote.dto.*
import com.payroll.android.domain.repository.AuthRepository
import com.payroll.android.domain.repository.EmployeeRepository
import com.payroll.android.domain.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class TimesheetState(
    val profile: EmployeeProfile? = null,
    val summary: SummaryResponse? = null,
    val timesheetEntries: List<TimesheetEntry> = emptyList(),
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val page: Int = 1,
    val hasMore: Boolean = true,
    val selectedMonth: YearMonth = YearMonth.now(),
    val error: String? = null,
    val unreadCount: Int = 0,
    val passwordError: String? = null,
    val passwordSuccess: Boolean = false,
    val isChangingPassword: Boolean = false,
    val showPasswordSheet: Boolean = false,
    val showNotificationSheet: Boolean = false,
    val notifications: List<NotificationItem> = emptyList(),
    val payableLimitPercentage: Double? = null
)

@HiltViewModel
class TimesheetViewModel @Inject constructor(
    private val employeeRepo: EmployeeRepository,
    private val authRepo: AuthRepository,
    private val notifRepo: NotificationRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TimesheetState())
    val state = _state.asStateFlow()

    init {
        loadAll()
    }

    private fun loadAll() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            // Profile
            employeeRepo.getProfile().onSuccess { profile ->
                _state.value = _state.value.copy(profile = profile)
            }
            // Summary
            employeeRepo.getSummary(4).onSuccess { summary ->
                _state.value = _state.value.copy(summary = summary)
            }
            // Setting for payable limit
            employeeRepo.getSetting("bulk_transfer_payment_percentage").onSuccess { pct ->
                _state.value = _state.value.copy(payableLimitPercentage = pct?.toDoubleOrNull())
            }
            // Timesheet
            loadTimesheet(reset = true)
            // Unread
            notifRepo.getUnreadCount().onSuccess { _state.value = _state.value.copy(unreadCount = it) }
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    fun loadTimesheet(reset: Boolean = false) {
        val s = _state.value
        if (!reset && !s.hasMore) return
        val page = if (reset) 1 else s.page
        viewModelScope.launch {
            if (!reset) _state.value = s.copy(isLoadingMore = true)
            val month = s.selectedMonth
            val from = month.atDay(1).format(DateTimeFormatter.ISO_DATE)
            val to = month.atEndOfMonth().format(DateTimeFormatter.ISO_DATE)
            employeeRepo.getTimesheet(from, to, page = page, pageSize = 50).onSuccess { result ->
                _state.value = _state.value.copy(
                    timesheetEntries = if (reset) result.items else s.timesheetEntries + result.items,
                    page = page + 1,
                    hasMore = page < result.totalPages,
                    isLoadingMore = false
                )
            }.onFailure {
                _state.value = _state.value.copy(isLoadingMore = false)
            }
        }
    }

    fun selectMonth(month: YearMonth) {
        _state.value = _state.value.copy(selectedMonth = month)
        loadTimesheet(reset = true)
    }

    fun changePassword(current: String, new: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isChangingPassword = true, passwordError = null)
            employeeRepo.changePassword(current, new)
                .onSuccess {
                    _state.value = _state.value.copy(isChangingPassword = false, passwordSuccess = true)
                }
                .onFailure {
                    _state.value = _state.value.copy(isChangingPassword = false, passwordError = it.message)
                }
        }
    }

    fun loadNotifications() {
        viewModelScope.launch {
            notifRepo.getNotifications(1, 20).onSuccess {
                _state.value = _state.value.copy(notifications = it.items)
            }
        }
    }

    fun markNotificationRead(id: Int) {
        viewModelScope.launch {
            notifRepo.markRead(id).onSuccess {
                _state.value = _state.value.copy(
                    notifications = _state.value.notifications.map {
                        if (it.id == id) it.copy(read = true) else it
                    },
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

    fun refreshUnread() {
        viewModelScope.launch {
            notifRepo.getUnreadCount().onSuccess { _state.value = _state.value.copy(unreadCount = it) }
        }
    }

    fun showPasswordSheet(show: Boolean) { _state.value = _state.value.copy(showPasswordSheet = show, passwordError = null, passwordSuccess = false) }
    fun showNotificationSheet(show: Boolean) { _state.value = _state.value.copy(showNotificationSheet = show) }

    fun logout() {
        viewModelScope.launch {
            authRepo.logout()
        }
    }

    fun getGreeting(): String {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        return when {
            hour < 12 -> "Chào buổi sáng"
            hour < 18 -> "Chào buổi chiều"
            else -> "Chào buổi tối"
        }
    }
}
