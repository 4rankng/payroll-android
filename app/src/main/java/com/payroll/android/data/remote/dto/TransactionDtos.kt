package com.payroll.android.data.remote.dto

import com.google.gson.annotations.SerializedName

data class Transaction(
    val id: Int,
    val description: String?,
    @SerializedName("transaction_type") val transactionType: String?,
    val amount: Double?,
    val party: String?,
    val status: String?,
    val url: String?,
    @SerializedName("asset_id") val assetId: Int?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?,
    val settlements: List<Settlement>? = null
)

data class CreateTransactionRequest(
    val description: String,
    @SerializedName("transaction_type") val transactionType: String,
    val amount: Double,
    val party: String? = null,
    val status: String? = null,
    val url: String? = null,
    @SerializedName("asset_id") val assetId: Int? = null
)

data class SettleRequest(
    val amount: Double,
    @SerializedName("settlement_date") val settlementDate: String?,
    @SerializedName("proof_url") val proofUrl: String? = null,
    @SerializedName("payment_method") val paymentMethod: String? = null,
    val notes: String? = null
)

data class Settlement(
    val id: Int,
    val amount: Double?,
    @SerializedName("settlement_date") val settlementDate: String?,
    @SerializedName("proof_url") val proofUrl: String?,
    @SerializedName("payment_method") val paymentMethod: String?,
    val notes: String?
)

data class TransactionMetadata(
    @SerializedName("transaction_types") val transactionTypes: List<String>?,
    val statuses: List<String>?
)
