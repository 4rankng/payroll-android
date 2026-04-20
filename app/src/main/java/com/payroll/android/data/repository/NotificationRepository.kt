package com.payroll.android.data.repository

import com.payroll.android.data.remote.ApiEndpoints
import com.payroll.android.data.remote.dto.*
import com.payroll.android.domain.repository.NotificationRepository
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val api: ApiEndpoints
) : NotificationRepository {

    override suspend fun getUnreadCount() = try {
        Result.success(api.getUnreadCount().count)
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun getNotifications(page: Int, pageSize: Int) = try {
        Result.success(api.getNotifications(page, pageSize))
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun markRead(id: Int) = try {
        api.markNotificationRead(id)
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun markAllRead() = try {
        api.markAllNotificationsRead()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }
}
