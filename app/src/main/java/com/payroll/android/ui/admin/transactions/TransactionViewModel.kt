package com.payroll.android.ui.admin.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payroll.android.data.remote.dto.*
import com.payroll.android.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TransactionListState(
    val isLoading: Boolean = true,
    val transactions: List<Transaction> = emptyList(),
    val typeFilter: String? = null,
    val statusFilter: String? = null,
    val page: Int = 1,
    val hasMore: Boolean = true,
    val isLoadingMore: Boolean = false,
    val metadata: TransactionMetadata? = null,
    val showDetail: Boolean = false,
    val selectedTransaction: Transaction? = null,
    val showSettleForm: Boolean = false,
    val settleAmount: String = "",
    val settleDate: String = "",
    val settleMethod: String = "",
    val settleNotes: String = "",
    val showReverseConfirm: Boolean = false,
    val error: String? = null
)

data class TransactionFormState(
    val isLoading: Boolean = false,
    val description: String = "",
    val transactionType: String = "",
    val amount: String = "",
    val party: String = "",
    val status: String = "pending",
    val error: String? = null,
    val success: Boolean = false
)

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionRepo: TransactionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TransactionListState())
    val state = _state.asStateFlow()

    private val _formState = MutableStateFlow(TransactionFormState())
    val formState = _formState.asStateFlow()

    init { loadAll() }

    fun loadAll() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            transactionRepo.getMetadata().onSuccess { _state.value = _state.value.copy(metadata = it) }
            loadTransactions(reset = true)
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    private suspend fun loadTransactions(reset: Boolean = false) {
        val s = _state.value
        val page = if (reset) 1 else s.page
        if (!reset) _state.value = s.copy(isLoadingMore = true)
        transactionRepo.getTransactions(page, 20, s.typeFilter, s.statusFilter, null, null)
            .onSuccess { result ->
                _state.value = _state.value.copy(
                    transactions = if (reset) result.items else s.transactions + result.items,
                    page = page + 1, hasMore = page < result.totalPages, isLoadingMore = false
                )
            }.onFailure { _state.value = _state.value.copy(isLoadingMore = false) }
    }

    fun onTypeFilter(type: String?) { _state.value = _state.value.copy(typeFilter = type); viewModelScope.launch { loadTransactions(reset = true) } }
    fun onStatusFilter(status: String?) { _state.value = _state.value.copy(statusFilter = status); viewModelScope.launch { loadTransactions(reset = true) } }
    fun loadMore() { viewModelScope.launch { loadTransactions() } }

    fun showDetail(tx: Transaction) { _state.value = _state.value.copy(showDetail = true, selectedTransaction = tx) }
    fun dismissDetail() { _state.value = _state.value.copy(showDetail = false, selectedTransaction = null, showSettleForm = false) }

    fun showSettleForm() { _state.value = _state.value.copy(showSettleForm = true) }
    fun onSettleField(field: String, value: String) {
        _state.value = when (field) {
            "amount" -> _state.value.copy(settleAmount = value)
            "date" -> _state.value.copy(settleDate = value)
            "method" -> _state.value.copy(settleMethod = value)
            "notes" -> _state.value.copy(settleNotes = value)
            else -> _state.value
        }
    }

    fun settleTransaction() {
        val id = _state.value.selectedTransaction?.id ?: return
        viewModelScope.launch {
            transactionRepo.settleTransaction(id, SettleRequest(
                amount = _state.value.settleAmount.toDoubleOrNull() ?: 0.0,
                settlementDate = _state.value.settleDate.ifBlank { null },
                paymentMethod = _state.value.settleMethod.ifBlank { null },
                notes = _state.value.settleNotes.ifBlank { null }
            )).onSuccess { dismissDetail(); loadAll() }
        }
    }

    fun showReverseConfirm() { _state.value = _state.value.copy(showReverseConfirm = true) }
    fun dismissReverseConfirm() { _state.value = _state.value.copy(showReverseConfirm = false) }

    fun reverseTransaction() {
        val id = _state.value.selectedTransaction?.id ?: return
        viewModelScope.launch {
            transactionRepo.reverseTransaction(id).onSuccess { dismissDetail(); loadAll() }
        }
    }

    // Form
    fun onFormField(field: String, value: String) {
        _formState.value = when (field) {
            "description" -> _formState.value.copy(description = value)
            "transactionType" -> _formState.value.copy(transactionType = value)
            "amount" -> _formState.value.copy(amount = value)
            "party" -> _formState.value.copy(party = value)
            "status" -> _formState.value.copy(status = value)
            else -> _formState.value
        }
    }

    fun saveTransaction(onSuccess: () -> Unit) {
        val s = _formState.value
        if (s.description.isBlank() || s.amount.isBlank()) {
            _formState.value = s.copy(error = "Vui lòng nhập đầy đủ thông tin"); return
        }
        viewModelScope.launch {
            _formState.value = s.copy(isLoading = true, error = null)
            transactionRepo.createTransaction(CreateTransactionRequest(
                description = s.description, transactionType = s.transactionType.ifBlank { "expense" },
                amount = s.amount.toDoubleOrNull() ?: 0.0, party = s.party.ifBlank { null }, status = s.status.ifBlank { null }
            )).onSuccess { _formState.value = _formState.value.copy(isLoading = false, success = true); onSuccess() }
                .onFailure { _formState.value = _formState.value.copy(isLoading = false, error = it.message) }
        }
    }
}
