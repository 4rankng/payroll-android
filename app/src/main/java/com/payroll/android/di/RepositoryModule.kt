package com.payroll.android.di

import com.payroll.android.data.remote.ApiEndpoints
import com.payroll.android.data.local.TokenManager
import com.payroll.android.data.repository.*
import com.payroll.android.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides @Singleton
    fun provideAuthRepository(api: ApiEndpoints, tokenManager: TokenManager): AuthRepository =
        AuthRepositoryImpl(api, tokenManager)

    @Provides @Singleton
    fun provideEmployeeRepository(api: ApiEndpoints): EmployeeRepository =
        EmployeeRepositoryImpl(api)

    @Provides @Singleton
    fun provideAdvancePaymentRepository(api: ApiEndpoints): AdvancePaymentRepository =
        AdvancePaymentRepositoryImpl(api)

    @Provides @Singleton
    fun provideNotificationRepository(api: ApiEndpoints): NotificationRepository =
        NotificationRepositoryImpl(api)
}
