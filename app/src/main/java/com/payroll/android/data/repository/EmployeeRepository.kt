package com.payroll.android.data.repository

import com.payroll.android.data.remote.ApiEndpoints
import com.payroll.android.data.remote.dto.*
import com.payroll.android.domain.repository.EmployeeRepository
import javax.inject.Inject

class EmployeeRepositoryImpl @Inject constructor(
    private val api: ApiEndpoints
) : EmployeeRepository {

    override suspend fun getProfile() = try {
        Result.success(api.getProfile())
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun changePassword(current: String, new: String) = try {
        api.changePassword(ChangePasswordRequest(current, new))
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun getTimesheet(fromDate: String?, toDate: String?, page: Int, pageSize: Int) = try {
        Result.success(api.getTimesheet(fromDate, toDate, page = page, pageSize = pageSize))
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun getSummary(weeks: Int) = try {
        Result.success(api.getSummary(weeks))
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun getSetting(key: String) = try {
        val result = api.getSetting(key)
        Result.success(result.value)
    } catch (e: Exception) { Result.failure(e) }
}
