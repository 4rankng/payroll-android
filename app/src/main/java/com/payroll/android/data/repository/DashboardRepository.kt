package com.payroll.android.data.repository

import com.payroll.android.data.remote.ApiEndpoints
import com.payroll.android.data.remote.dto.*
import com.payroll.android.domain.repository.DashboardRepository
import javax.inject.Inject

class DashboardRepositoryImpl @Inject constructor(
    private val api: ApiEndpoints
) : DashboardRepository {
    override suspend fun getSummary() = try { Result.success(api.getDashboardSummary()) } catch (e: Exception) { Result.failure(e) }
    override suspend fun getFinancialData(period: String) = try { Result.success(api.getFinancialData(period)) } catch (e: Exception) { Result.failure(e) }
    override suspend fun getRecentActivities() = try { Result.success(api.getRecentActivities()) } catch (e: Exception) { Result.failure(e) }
    override suspend fun getSalaryDistribution(month: String) = try { Result.success(api.getSalaryDistribution(month)) } catch (e: Exception) { Result.failure(e) }
    override suspend fun getProjectProfitability() = try { Result.success(api.getProjectProfitability()) } catch (e: Exception) { Result.failure(e) }
    override suspend fun getMonthlyFinancials() = try { Result.success(api.getMonthlyFinancials()) } catch (e: Exception) { Result.failure(e) }
    override suspend fun getPartnerSummary() = try { Result.success(api.getPartnerSummary()) } catch (e: Exception) { Result.failure(e) }
}
