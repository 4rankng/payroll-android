package com.payroll.android.data.repository

import com.payroll.android.data.remote.ApiEndpoints
import com.payroll.android.data.remote.dto.*
import com.payroll.android.domain.repository.TransactionRepository
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val api: ApiEndpoints
) : TransactionRepository {
    override suspend fun getTransactions(page: Int, pageSize: Int, type: String?, status: String?, fromDate: String?, toDate: String?) =
        try { Result.success(api.getTransactions(page, pageSize, type, status, fromDate, toDate)) } catch (e: Exception) { Result.failure(e) }
    override suspend fun getTransaction(id: Int) = try { Result.success(api.getTransaction(id)) } catch (e: Exception) { Result.failure(e) }
    override suspend fun createTransaction(request: CreateTransactionRequest) = try { Result.success(api.createTransaction(request)) } catch (e: Exception) { Result.failure(e) }
    override suspend fun settleTransaction(id: Int, request: SettleRequest) = try { api.settleTransaction(id, request); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }
    override suspend fun reverseTransaction(id: Int) = try { api.reverseTransaction(id); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }
    override suspend fun getMetadata() = try { Result.success(api.getTransactionMetadata()) } catch (e: Exception) { Result.failure(e) }
}
