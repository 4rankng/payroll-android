package com.payroll.android.ui.admin.loans

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payroll.android.data.remote.dto.*
import com.payroll.android.domain.repository.LoanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoanListState(
    val isLoading: Boolean = true,
    val loans: List<Loan> = emptyList(),
    val lenders: List<Lender> = emptyList(),
    val page: Int = 1,
    val hasMore: Boolean = true,
    val isLoadingMore: Boolean = false,
    val selectedLoan: Loan? = null,
    val schedule: List<RepaymentSchedule> = emptyList(),
    val showDetail: Boolean = false,
    val showLenderDialog: Boolean = false,
    val lenderName: String = "",
    val lenderContact: String = "",
    val showDisburseForm: Boolean = false,
    val showRepayForm: Boolean = false,
    val repayAmount: String = "",
    val error: String? = null
)

data class LoanFormState(
    val isLoading: Boolean = false,
    val lenderId: String = "",
    val amount: String = "",
    val interestRate: String = "",
    val termMonths: String = "",
    val loanType: String = "fixed",
    val disbursementDate: String = "",
    val error: String? = null,
    val success: Boolean = false
)

@HiltViewModel
class LoanViewModel @Inject constructor(
    private val loanRepo: LoanRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoanListState())
    val state = _state.asStateFlow()

    private val _formState = MutableStateFlow(LoanFormState())
    val formState = _formState.asStateFlow()

    init { loadAll() }

    fun loadAll() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            loadLoans(reset = true)
            loanRepo.getLenders(1, 100).onSuccess { _state.value = _state.value.copy(lenders = it.items) }
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    private suspend fun loadLoans(reset: Boolean = false) {
        val s = _state.value
        val page = if (reset) 1 else s.page
        if (!reset) _state.value = s.copy(isLoadingMore = true)
        loanRepo.getLoans(page, 20).onSuccess { result ->
            _state.value = _state.value.copy(
                loans = if (reset) result.items else s.loans + result.items,
                page = page + 1, hasMore = page < result.totalPages, isLoadingMore = false
            )
        }.onFailure { _state.value = _state.value.copy(isLoadingMore = false) }
    }

    fun loadMore() { viewModelScope.launch { loadLoans() } }

    fun loadDetail(loan: Loan) {
        _state.value = _state.value.copy(selectedLoan = loan, showDetail = true)
        viewModelScope.launch {
            loanRepo.getLoanSchedule(loan.id).onSuccess { _state.value = _state.value.copy(schedule = it) }
        }
    }

    fun dismissDetail() { _state.value = _state.value.copy(showDetail = false, selectedLoan = null, schedule = emptyList(), showDisburseForm = false, showRepayForm = false) }

    fun showLenderDialog() { _state.value = _state.value.copy(showLenderDialog = true, lenderName = "", lenderContact = "") }
    fun dismissLenderDialog() { _state.value = _state.value.copy(showLenderDialog = false) }
    fun onLenderField(field: String, value: String) {
        _state.value = when (field) { "name" -> _state.value.copy(lenderName = value); "contact" -> _state.value.copy(lenderContact = value); else -> _state.value }
    }

    fun createLender() {
        viewModelScope.launch {
            loanRepo.createLender(CreateLenderRequest(name = _state.value.lenderName, contact = _state.value.lenderContact.ifBlank { null }))
                .onSuccess { dismissLenderDialog(); loadAll() }
        }
    }

    fun deleteLender(id: Int) {
        viewModelScope.launch { loanRepo.deleteLender(id).onSuccess { loadAll() } }
    }

    fun disburseLoan() {
        val loan = _state.value.selectedLoan ?: return
        viewModelScope.launch {
            loanRepo.disburseLoan(loan.id, DisburseRequest()).onSuccess { dismissDetail(); loadAll() }
        }
    }

    fun showRepayForm() { _state.value = _state.value.copy(showRepayForm = true, repayAmount = "") }
    fun onRepayAmountChange(v: String) { _state.value = _state.value.copy(repayAmount = v) }
    fun repayLoan() {
        val loan = _state.value.selectedLoan ?: return
        viewModelScope.launch {
            loanRepo.repayLoan(loan.id, RepayRequest(amount = _state.value.repayAmount.toDoubleOrNull() ?: 0.0))
                .onSuccess { dismissDetail(); loadAll() }
        }
    }

    // Loan form
    fun onFormField(field: String, value: String) {
        _formState.value = when (field) {
            "lenderId" -> _formState.value.copy(lenderId = value)
            "amount" -> _formState.value.copy(amount = value)
            "interestRate" -> _formState.value.copy(interestRate = value)
            "termMonths" -> _formState.value.copy(termMonths = value)
            "loanType" -> _formState.value.copy(loanType = value)
            "disbursementDate" -> _formState.value.copy(disbursementDate = value)
            else -> _formState.value
        }
    }

    fun saveLoan(onSuccess: () -> Unit) {
        val s = _formState.value
        viewModelScope.launch {
            _formState.value = s.copy(isLoading = true, error = null)
            loanRepo.createLoan(CreateLoanRequest(
                lenderId = s.lenderId.toIntOrNull() ?: 0, amount = s.amount.toDoubleOrNull() ?: 0.0,
                interestRate = s.interestRate.toDoubleOrNull() ?: 0.0, termMonths = s.termMonths.toIntOrNull() ?: 0,
                loanType = s.loanType, disbursementDate = s.disbursementDate
            )).onSuccess { _formState.value = _formState.value.copy(isLoading = false, success = true); onSuccess() }
                .onFailure { _formState.value = _formState.value.copy(isLoading = false, error = it.message) }
        }
    }
}
