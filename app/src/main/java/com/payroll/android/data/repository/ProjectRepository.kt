package com.payroll.android.data.repository

import com.payroll.android.data.remote.ApiEndpoints
import com.payroll.android.data.remote.dto.*
import com.payroll.android.domain.repository.ProjectRepository
import javax.inject.Inject

class ProjectRepositoryImpl @Inject constructor(
    private val api: ApiEndpoints
) : ProjectRepository {
    override suspend fun getSummary() = try { Result.success(api.getProjectSummary()) } catch (e: Exception) { Result.failure(e) }
    override suspend fun getProjects(page: Int, pageSize: Int, search: String?, status: String?, partner: Boolean?) =
        try { Result.success(api.getProjects(page, pageSize, search, status, partner)) } catch (e: Exception) { Result.failure(e) }
    override suspend fun getProject(id: Int) = try { Result.success(api.getProject(id)) } catch (e: Exception) { Result.failure(e) }
    override suspend fun createProject(request: CreateProjectRequest) = try { Result.success(api.createProject(request)) } catch (e: Exception) { Result.failure(e) }
    override suspend fun updateProject(id: Int, request: UpdateProjectRequest) = try { Result.success(api.updateProject(id, request)) } catch (e: Exception) { Result.failure(e) }
    override suspend fun updateStatus(id: Int, status: String) = try { Result.success(api.updateProjectStatus(id, ProjectStatusRequest(status))) } catch (e: Exception) { Result.failure(e) }
    override suspend fun getProjectEmployees(id: Int) = try { Result.success(api.getProjectEmployees(id)) } catch (e: Exception) { Result.failure(e) }
    override suspend fun assignEmployee(projectId: Int, request: AssignEmployeeRequest) = try { api.assignEmployee(projectId, request); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }
    override suspend fun removeEmployee(projectId: Int, request: AssignEmployeeRequest) = try { api.removeEmployee(projectId, request); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }
    override suspend fun bulkAssign(projectId: Int, request: BulkAssignRequest) = try { api.bulkAssign(projectId, request); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }
    override suspend fun getProjectFinancial(id: Int) = try { Result.success(api.getProjectFinancial(id)) } catch (e: Exception) { Result.failure(e) }
    override suspend fun getActivePayrate(projectId: Int) = try { Result.success(api.getActivePayrate(projectId)) } catch (e: Exception) { Result.failure(e) }
    override suspend fun createPayrate(projectId: Int, request: CreatePayrateRequest) = try { Result.success(api.createPayrate(projectId, request)) } catch (e: Exception) { Result.failure(e) }
    override suspend fun updatePayrate(projectId: Int, payrateId: Int, request: UpdatePayrateRequest) = try { Result.success(api.updatePayrate(projectId, payrateId, request)) } catch (e: Exception) { Result.failure(e) }
}
