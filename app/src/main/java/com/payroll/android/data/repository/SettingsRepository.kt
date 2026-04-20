package com.payroll.android.data.repository

import com.payroll.android.data.remote.ApiEndpoints
import com.payroll.android.data.remote.dto.*
import com.payroll.android.domain.repository.SettingsRepository
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val api: ApiEndpoints
) : SettingsRepository {
    override suspend fun getSettings() = try { Result.success(api.getSettings()) } catch (e: Exception) { Result.failure(e) }
    override suspend fun updateSetting(id: Int, value: String) = try { Result.success(api.updateSetting(id, UpdateSettingRequest(value))) } catch (e: Exception) { Result.failure(e) }
    override suspend fun sendNotification(request: SendNotificationRequest) = try { api.sendNotification(request); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }
    override suspend fun sendEmail(request: SendEmailRequest) = try { api.sendEmail(request); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }
    override suspend fun getEmailHistory() = try { Result.success(api.getEmailHistory()) } catch (e: Exception) { Result.failure(e) }
}
