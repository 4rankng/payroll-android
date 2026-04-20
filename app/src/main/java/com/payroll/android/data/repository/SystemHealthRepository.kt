package com.payroll.android.data.repository

import com.payroll.android.data.remote.ApiEndpoints
import com.payroll.android.data.remote.dto.*
import com.payroll.android.domain.repository.SystemHealthRepository
import javax.inject.Inject

class SystemHealthRepositoryImpl @Inject constructor(
    private val api: ApiEndpoints
) : SystemHealthRepository {
    override suspend fun getApiMetrics() = try { Result.success(api.getApiMetrics()) } catch (e: Exception) { Result.failure(e) }
    override suspend fun getRecentErrors() = try { Result.success(api.getRecentErrors()) } catch (e: Exception) { Result.failure(e) }
    override suspend fun getSlowestEndpoints() = try { Result.success(api.getSlowestEndpoints()) } catch (e: Exception) { Result.failure(e) }
}
