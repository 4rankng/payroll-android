package com.payroll.android.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "payroll_prefs")

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val KEY_TOKEN = stringPreferencesKey("access_token")
        private val KEY_USERNAME = stringPreferencesKey("username")
        private val KEY_ROLE = stringPreferencesKey("user_role")
        private val KEY_FULLNAME = stringPreferencesKey("user_fullname")
    }

    val token: Flow<String?> = context.dataStore.data.map { it[KEY_TOKEN] }
    val username: Flow<String?> = context.dataStore.data.map { it[KEY_USERNAME] }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { it[KEY_TOKEN] = token }
    }

    suspend fun saveUsername(username: String) {
        context.dataStore.edit { it[KEY_USERNAME] = username }
    }

    suspend fun saveRole(role: String) {
        context.dataStore.edit { it[KEY_ROLE] = role }
    }

    suspend fun saveFullName(name: String) {
        context.dataStore.edit { it[KEY_FULLNAME] = name }
    }

    fun getRole(): Flow<String?> = context.dataStore.data.map { it[KEY_ROLE] }
    fun getFullName(): Flow<String?> = context.dataStore.data.map { it[KEY_FULLNAME] }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
