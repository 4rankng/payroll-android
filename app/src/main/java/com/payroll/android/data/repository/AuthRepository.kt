package com.payroll.android.data.repository

import com.payroll.android.data.local.TokenManager
import com.payroll.android.data.remote.ApiEndpoints
import com.payroll.android.data.remote.dto.*
import com.payroll.android.domain.repository.AuthRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: ApiEndpoints,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun login(username: String, password: String): Result<LoginResponse> {
        return try {
            val response = api.login(LoginRequest(username, password))
            tokenManager.saveToken(response.accessToken)
            tokenManager.saveUsername(username)
            response.user.role?.let { tokenManager.saveRole(it) }
            response.user.fullname?.let { tokenManager.saveFullName(it) }
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        tokenManager.clear()
    }

    override suspend fun isLoggedIn(): Boolean {
        return tokenManager.token.firstOrNull() != null
    }
}
