package com.payroll.android.data.repository

import com.payroll.android.data.remote.ApiEndpoints
import com.payroll.android.data.remote.dto.*
import com.payroll.android.domain.repository.LoanRepository
import javax.inject.Inject

class LoanRepositoryImpl @Inject constructor(
    private val api: ApiEndpoints
) : LoanRepository {
    override suspend fun getLenders(page: Int, pageSize: Int) = try { Result.success(api.getLenders(page, pageSize)) } catch (e: Exception) { Result.failure(e) }
    override suspend fun createLender(request: CreateLenderRequest) = try { Result.success(api.createLender(request)) } catch (e: Exception) { Result.failure(e) }
    override suspend fun updateLender(id: Int, request: UpdateLenderRequest) = try { Result.success(api.updateLender(id, request)) } catch (e: Exception) { Result.failure(e) }
    override suspend fun deleteLender(id: Int) = try { api.deleteLender(id); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }
    override suspend fun getLoans(page: Int, pageSize: Int) = try { Result.success(api.getLoans(page, pageSize)) } catch (e: Exception) { Result.failure(e) }
    override suspend fun createLoan(request: CreateLoanRequest) = try { Result.success(api.createLoan(request)) } catch (e: Exception) { Result.failure(e) }
    override suspend fun disburseLoan(id: Int, request: DisburseRequest) = try { api.disburseLoan(id, request); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }
    override suspend fun repayLoan(id: Int, request: RepayRequest) = try { api.repayLoan(id, request); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }
    override suspend fun getLoanSchedule(id: Int) = try { Result.success(api.getLoanSchedule(id)) } catch (e: Exception) { Result.failure(e) }
}
