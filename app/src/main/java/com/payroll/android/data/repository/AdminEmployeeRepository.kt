package com.payroll.android.data.repository

import com.payroll.android.data.remote.ApiEndpoints
import com.payroll.android.data.remote.dto.*
import com.payroll.android.domain.repository.AdminEmployeeRepository
import javax.inject.Inject

class AdminEmployeeRepositoryImpl @Inject constructor(
    private val api: ApiEndpoints
) : AdminEmployeeRepository {
    override suspend fun getSummary() = try { Result.success(api.getEmployeeSummary()) } catch (e: Exception) { Result.failure(e) }
    override suspend fun getEmployees(page: Int, pageSize: Int, search: String?, status: String?, projectId: Int?) =
        try { Result.success(api.getEmployees(page, pageSize, search, status, projectId)) } catch (e: Exception) { Result.failure(e) }
    override suspend fun getEmployee(id: Int) = try { Result.success(api.getEmployee(id)) } catch (e: Exception) { Result.failure(e) }
    override suspend fun createEmployee(request: CreateEmployeeRequest) = try { Result.success(api.createEmployee(request)) } catch (e: Exception) { Result.failure(e) }
    override suspend fun updateEmployee(id: Int, request: UpdateEmployeeRequest) = try { Result.success(api.updateEmployee(id, request)) } catch (e: Exception) { Result.failure(e) }
    override suspend fun deleteEmployee(id: Int) = try { api.deleteEmployee(id); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }
    override suspend fun getEmployeeProjects(id: Int) = try { Result.success(api.getEmployeeProjects(id)) } catch (e: Exception) { Result.failure(e) }
    override suspend fun getEmployeeTimesheetSummary(id: Int) = try { Result.success(api.getEmployeeTimesheetSummary(id)) } catch (e: Exception) { Result.failure(e) }
    override suspend fun getEmployeePayroll(id: Int) = try { Result.success(api.getEmployeePayroll(id)) } catch (e: Exception) { Result.failure(e) }
    override suspend fun getMissingBankDetails() = try { Result.success(api.getMissingBankDetails()) } catch (e: Exception) { Result.failure(e) }
}
