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

    @Provides @Singleton
    fun provideDashboardRepository(api: ApiEndpoints): DashboardRepository =
        DashboardRepositoryImpl(api)

    @Provides @Singleton
    fun provideAdminEmployeeRepository(api: ApiEndpoints): AdminEmployeeRepository =
        AdminEmployeeRepositoryImpl(api)

    @Provides @Singleton
    fun provideProjectRepository(api: ApiEndpoints): ProjectRepository =
        ProjectRepositoryImpl(api)

    @Provides @Singleton
    fun provideAdminTimesheetRepository(api: ApiEndpoints): AdminTimesheetRepository =
        AdminTimesheetRepositoryImpl(api)

    @Provides @Singleton
    fun provideTransactionRepository(api: ApiEndpoints): TransactionRepository =
        TransactionRepositoryImpl(api)

    @Provides @Singleton
    fun provideLedgerRepository(api: ApiEndpoints): LedgerRepository =
        LedgerRepositoryImpl(api)

    @Provides @Singleton
    fun provideLoanRepository(api: ApiEndpoints): LoanRepository =
        LoanRepositoryImpl(api)

    @Provides @Singleton
    fun provideAdminAdvancePaymentRepository(api: ApiEndpoints): AdminAdvancePaymentRepository =
        AdminAdvancePaymentRepositoryImpl(api)

    @Provides @Singleton
    fun provideUserRepository(api: ApiEndpoints): UserRepository =
        UserRepositoryImpl(api)

    @Provides @Singleton
    fun provideSettingsRepository(api: ApiEndpoints): SettingsRepository =
        SettingsRepositoryImpl(api)

    @Provides @Singleton
    fun provideSystemHealthRepository(api: ApiEndpoints): SystemHealthRepository =
        SystemHealthRepositoryImpl(api)
}
