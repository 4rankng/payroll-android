package com.payroll.android.data.repository

import com.payroll.android.data.remote.ApiEndpoints
import com.payroll.android.data.remote.dto.*
import com.payroll.android.domain.repository.LedgerRepository
import javax.inject.Inject

class LedgerRepositoryImpl @Inject constructor(
    private val api: ApiEndpoints
) : LedgerRepository {
    override suspend fun getEntries(page: Int, pageSize: Int) = try { Result.success(api.getLedgerEntries(page, pageSize)) } catch (e: Exception) { Result.failure(e) }
    override suspend fun getEntry(id: Int) = try { Result.success(api.getLedgerEntry(id)) } catch (e: Exception) { Result.failure(e) }
    override suspend fun reverseEntry(id: Int) = try { api.reverseLedgerEntry(id); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }
    override suspend fun getBalance() = try { Result.success(api.getLedgerBalance()) } catch (e: Exception) { Result.failure(e) }
    override suspend fun getSummary() = try { Result.success(api.getLedgerSummary()) } catch (e: Exception) { Result.failure(e) }
    override suspend fun getCashFlow() = try { Result.success(api.getCashFlow()) } catch (e: Exception) { Result.failure(e) }
}
