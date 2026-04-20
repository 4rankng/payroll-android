package com.payroll.android.ui.admin.advance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payroll.android.data.remote.dto.*
import com.payroll.android.domain.repository.AdminAdvancePaymentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdvancePaymentAdminState(
    val isLoading: Boolean = true,
    val summary: AdvancePaymentSummary? = null,
    val advancePayments: List<AdminAdvancePayment> = emptyList(),
    val statusFilter: String? = null,
    val page: Int = 1,
    val hasMore: Boolean = true,
    val isLoadingMore: Boolean = false,
    val showCancelConfirm: Boolean = false,
    val cancelTargetId: Int? = null,
    val error: String? = null
)

@HiltViewModel
class AdvancePaymentAdminViewModel @Inject constructor(
    private val advanceRepo: AdminAdvancePaymentRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdvancePaymentAdminState())
    val state = _state.asStateFlow()

    init { loadAll() }

    fun loadAll() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            advanceRepo.getSummary().onSuccess { _state.value = _state.value.copy(summary = it) }
            loadPayments(reset = true)
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    private suspend fun loadPayments(reset: Boolean = false) {
        val s = _state.value
        val page = if (reset) 1 else s.page
        if (!reset) _state.value = s.copy(isLoadingMore = true)
        advanceRepo.getAdvancePayments(page, 20, s.statusFilter).onSuccess { result ->
            _state.value = _state.value.copy(
                advancePayments = if (reset) result.items else s.advancePayments + result.items,
                page = page + 1, hasMore = page < result.totalPages, isLoadingMore = false
            )
        }.onFailure { _state.value = _state.value.copy(isLoadingMore = false) }
    }

    fun onStatusFilter(status: String?) { _state.value = _state.value.copy(statusFilter = status); viewModelScope.launch { loadPayments(reset = true) } }
    fun loadMore() { viewModelScope.launch { loadPayments() } }

    fun showCancelConfirm(id: Int) { _state.value = _state.value.copy(showCancelConfirm = true, cancelTargetId = id) }
    fun dismissCancelConfirm() { _state.value = _state.value.copy(showCancelConfirm = false, cancelTargetId = null) }

    fun cancelPayment() {
        val id = _state.value.cancelTargetId ?: return
        viewModelScope.launch { advanceRepo.cancelAdvancePayment(id).onSuccess { dismissCancelConfirm(); loadAll() } }
    }
}
