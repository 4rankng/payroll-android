package com.payroll.android.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LedgerEntry(
    val id: Int,
    val description: String?,
    @SerializedName("account_type") val accountType: String?,
    @SerializedName("debit_amount") val debitAmount: Double?,
    @SerializedName("credit_amount") val creditAmount: Double?,
    val balance: Double?,
    val reference: String?,
    @SerializedName("created_at") val createdAt: String?,
    val status: String?
)

data class LedgerBalance(
    @SerializedName("total_debit") val totalDebit: Double,
    @SerializedName("total_credit") val totalCredit: Double,
    val balance: Double
)

data class LedgerSummary(
    @SerializedName("total_entries") val totalEntries: Int,
    @SerializedName("total_debit") val totalDebit: Double,
    @SerializedName("total_credit") val totalCredit: Double,
    val balance: Double
)

data class CashFlowEntry(
    val month: String?,
    @SerializedName("cash_in") val cashIn: Double?,
    @SerializedName("cash_out") val cashOut: Double?,
    @SerializedName("net_flow") val netFlow: Double?
)

data class AccountMetadata(
    val types: List<String>?
)
