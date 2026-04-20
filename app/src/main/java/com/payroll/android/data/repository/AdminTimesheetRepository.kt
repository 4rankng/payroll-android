package com.payroll.android.data.repository

import com.payroll.android.data.remote.ApiEndpoints
import com.payroll.android.data.remote.dto.*
import com.payroll.android.domain.repository.AdminTimesheetRepository
import javax.inject.Inject

class AdminTimesheetRepositoryImpl @Inject constructor(
    private val api: ApiEndpoints
) : AdminTimesheetRepository {
    override suspend fun getSummary() = try { Result.success(api.getTimesheetSummary()) } catch (e: Exception) { Result.failure(e) }
    override suspend fun getTimesheets(page: Int, pageSize: Int, status: String?, projectId: Int?, employeeId: Int?, fromDate: String?, toDate: String?) =
        try { Result.success(api.getAdminTimesheets(page, pageSize, status, projectId, employeeId, fromDate, toDate)) } catch (e: Exception) { Result.failure(e) }
    override suspend fun getTimesheetDetail(id: Int) = try { Result.success(api.getTimesheetDetail(id)) } catch (e: Exception) { Result.failure(e) }
    override suspend fun createTimesheet(request: CreateTimesheetRequest) = try { Result.success(api.createTimesheet(request)) } catch (e: Exception) { Result.failure(e) }
    override suspend fun updateTimesheet(id: Int, request: UpdateTimesheetRequest) = try { Result.success(api.updateTimesheet(id, request)) } catch (e: Exception) { Result.failure(e) }
    override suspend fun deleteTimesheet(id: Int) = try { api.deleteTimesheet(id); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }
    override suspend fun approveTimesheet(id: Int) = try { api.approveTimesheet(id); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }
    override suspend fun rejectTimesheet(id: Int, reason: String) = try { api.rejectTimesheet(id, RejectRequest(reason)); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }
    override suspend fun bulkApprove(ids: List<Int>) = try { api.bulkApprove(BulkActionRequest(ids)); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }
    override suspend fun bulkReject(ids: List<Int>) = try { api.bulkReject(BulkActionRequest(ids)); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }
    override suspend fun approveAll() = try { api.approveAllTimesheets(); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }
    override suspend fun getGroupedTimesheets(projectId: Int?) = try { Result.success(api.getGroupedTimesheets(projectId)) } catch (e: Exception) { Result.failure(e) }
}
