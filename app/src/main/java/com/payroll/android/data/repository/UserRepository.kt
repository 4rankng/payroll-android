package com.payroll.android.data.repository

import com.payroll.android.data.remote.ApiEndpoints
import com.payroll.android.data.remote.dto.*
import com.payroll.android.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val api: ApiEndpoints
) : UserRepository {
    override suspend fun getSummary() = try { Result.success(api.getUserSummary()) } catch (e: Exception) { Result.failure(e) }
    override suspend fun getUsers(page: Int, pageSize: Int, search: String?, role: String?) =
        try { Result.success(api.getUsers(page, pageSize, search, role)) } catch (e: Exception) { Result.failure(e) }
    override suspend fun createUser(request: CreateUserRequest) = try { Result.success(api.createUser(request)) } catch (e: Exception) { Result.failure(e) }
    override suspend fun updateUser(id: Int, request: UpdateUserRequest) = try { Result.success(api.updateUser(id, request)) } catch (e: Exception) { Result.failure(e) }
    override suspend fun suspendUser(id: Int) = try { api.suspendUser(id); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }
    override suspend fun activateUser(id: Int) = try { api.activateUser(id); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }
    override suspend fun resetPassword(id: Int) = try { api.resetUserPassword(id); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }
}
