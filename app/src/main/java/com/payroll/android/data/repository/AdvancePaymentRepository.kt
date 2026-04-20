package com.payroll.android.data.repository

import com.payroll.android.data.remote.ApiEndpoints
import com.payroll.android.data.remote.dto.*
import com.payroll.android.domain.repository.AdvancePaymentRepository
import javax.inject.Inject

class AdvancePaymentRepositoryImpl @Inject constructor(
    private val api: ApiEndpoints
) : AdvancePaymentRepository {

    override suspend fun getInfo() = try {
        Result.success(api.getAdvancePaymentInfo())
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun requestAdvance(amount: Double) = try {
        Result.success(api.requestAdvancePayment(AdvancePaymentRequest(amount)))
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun getHistory(page: Int, pageSize: Int) = try {
        Result.success(api.getAdvancePaymentHistory(page, pageSize))
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun calculateFee(amount: Double) = try {
        Result.success(api.calculateFee(FeeCalculationRequest(amount)))
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun cancelRequest(id: Int) = try {
        Result.success(api.cancelAdvanceRequest(id))
    } catch (e: Exception) { Result.failure(e) }
}
