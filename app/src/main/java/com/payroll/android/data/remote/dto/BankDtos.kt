package com.payroll.android.data.remote.dto

import com.google.gson.annotations.SerializedName

data class BankDetail(
    val id: Int,
    @SerializedName("employee_id") val employeeId: Int,
    @SerializedName("employee_name") val employeeName: String?,
    val bank: String?,
    @SerializedName("bank_account_number") val bankAccountNumber: String?,
    @SerializedName("bank_account_name") val bankAccountName: String?,
    val missing: Boolean = false
)

data class AssetUploadResponse(
    val id: Int?,
    val url: String?
)
