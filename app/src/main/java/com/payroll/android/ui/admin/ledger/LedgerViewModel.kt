package com.payroll.android.ui.admin.ledger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payroll.android.data.remote.dto.*
import com.payroll.android.domain.repository.LedgerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LedgerState(
    val isLoading: Boolean = true,
    val entries: List<LedgerEntry> = emptyList(),
    val balance: LedgerBalance? = null,
    val summary: LedgerSummary? = null,
    val cashFlow: List<CashFlowEntry> = emptyList(),
    val page: Int = 1,
    val hasMore: Boolean = true,
    val isLoadingMore: Boolean = false,
    val showReverseConfirm: Boolean = false,
    val reverseTargetId: Int? = null,
    val error: String? = null
)

@HiltViewModel
class LedgerViewModel @Inject constructor(
    private val ledgerRepo: LedgerRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LedgerState())
    val state = _state.asStateFlow()

    init { loadAll() }

    fun loadAll() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            ledgerRepo.getBalance().onSuccess { _state.value = _state.value.copy(balance = it) }
            ledgerRepo.getSummary().onSuccess { _state.value = _state.value.copy(summary = it) }
            ledgerRepo.getCashFlow().onSuccess { _state.value = _state.value.copy(cashFlow = it) }
            loadEntries(reset = true)
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    private suspend fun loadEntries(reset: Boolean = false) {
        val s = _state.value
        val page = if (reset) 1 else s.page
        if (!reset) _state.value = s.copy(isLoadingMore = true)
        ledgerRepo.getEntries(page, 20).onSuccess { result ->
            _state.value = _state.value.copy(
                entries = if (reset) result.items else s.entries + result.items,
                page = page + 1, hasMore = page < result.totalPages, isLoadingMore = false
            )
        }.onFailure { _state.value = _state.value.copy(isLoadingMore = false) }
    }

    fun loadMore() { viewModelScope.launch { loadEntries() } }

    fun showReverseConfirm(id: Int) { _state.value = _state.value.copy(showReverseConfirm = true, reverseTargetId = id) }
    fun dismissReverseConfirm() { _state.value = _state.value.copy(showReverseConfirm = false, reverseTargetId = null) }

    fun reverseEntry() {
        val id = _state.value.reverseTargetId ?: return
        viewModelScope.launch {
            ledgerRepo.reverseEntry(id).onSuccess { dismissReverseConfirm(); loadAll() }
        }
    }
}
