package com.payroll.android.data.repository

import com.payroll.android.data.remote.ApiEndpoints
import com.payroll.android.data.remote.dto.*
import com.payroll.android.domain.repository.AdminAdvancePaymentRepository
import javax.inject.Inject

class AdminAdvancePaymentRepositoryImpl @Inject constructor(
    private val api: ApiEndpoints
) : AdminAdvancePaymentRepository {
    override suspend fun getSummary() = try { Result.success(api.getAdminAdvanceSummary()) } catch (e: Exception) { Result.failure(e) }
    override suspend fun getAdvancePayments(page: Int, pageSize: Int, status: String?) =
        try { Result.success(api.getAdminAdvancePayments(page, pageSize, status)) } catch (e: Exception) { Result.failure(e) }
    override suspend fun cancelAdvancePayment(id: Int) = try { api.cancelAdminAdvancePayment(id); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }
    override suspend fun getEligibleEmployees() = try { Result.success(api.getEligibleEmployees()) } catch (e: Exception) { Result.failure(e) }
    override suspend fun getAvailableMonths() = try { Result.success(api.getAvailableMonths()) } catch (e: Exception) { Result.failure(e) }
}
