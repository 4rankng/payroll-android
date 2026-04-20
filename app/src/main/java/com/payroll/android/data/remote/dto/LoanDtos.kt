package com.payroll.android.data.remote.dto

import com.google.gson.annotations.SerializedName

data class Lender(
    val id: Int,
    val name: String?,
    val contact: String?,
    val note: String?,
    @SerializedName("created_at") val createdAt: String?
)

data class CreateLenderRequest(
    val name: String,
    val contact: String? = null,
    val note: String? = null
)

data class UpdateLenderRequest(
    val name: String? = null,
    val contact: String? = null,
    val note: String? = null
)

data class Loan(
    val id: Int,
    @SerializedName("lender_id") val lenderId: Int?,
    @SerializedName("lender_name") val lenderName: String?,
    val amount: Double?,
    @SerializedName("interest_rate") val interestRate: Double?,
    @SerializedName("term_months") val termMonths: Int?,
    @SerializedName("loan_type") val loanType: String?,
    val status: String?,
    @SerializedName("disbursement_date") val disbursementDate: String?,
    @SerializedName("monthly_payment") val monthlyPayment: Double?,
    @SerializedName("remaining_balance") val remainingBalance: Double?,
    @SerializedName("created_at") val createdAt: String?
)

data class CreateLoanRequest(
    @SerializedName("lender_id") val lenderId: Int,
    val amount: Double,
    @SerializedName("interest_rate") val interestRate: Double,
    @SerializedName("term_months") val termMonths: Int,
    @SerializedName("loan_type") val loanType: String,
    @SerializedName("disbursement_date") val disbursementDate: String
)

data class DisburseRequest(
    val amount: Double? = null,
    val notes: String? = null
)

data class RepayRequest(
    val amount: Double,
    val notes: String? = null
)

data class RepaymentSchedule(
    val month: Int?,
    @SerializedName("due_date") val dueDate: String?,
    @SerializedName("payment_amount") val paymentAmount: Double?,
    @SerializedName("principal") val principal: Double?,
    val interest: Double?,
    @SerializedName("remaining_balance") val remainingBalance: Double?,
    val status: String?
)
