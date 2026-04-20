package com.payroll.android.ui.admin.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payroll.android.data.remote.dto.*
import com.payroll.android.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsState(
    val isLoading: Boolean = true,
    val settings: List<Setting> = emptyList(),
    val editingSetting: Setting? = null,
    val editValue: String = "",
    val showEditDialog: Boolean = false,
    val showNotificationForm: Boolean = false,
    val showEmailForm: Boolean = false,
    val notifTitle: String = "",
    val notifMessage: String = "",
    val notifUserId: String = "",
    val emailRecipients: String = "",
    val emailSubject: String = "",
    val emailBody: String = "",
    val emailHistory: List<EmailHistoryItem> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepo: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    init { loadAll() }

    fun loadAll() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            settingsRepo.getSettings().onSuccess { _state.value = _state.value.copy(settings = it) }
            settingsRepo.getEmailHistory().onSuccess { _state.value = _state.value.copy(emailHistory = it) }
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    fun showEditDialog(setting: Setting) {
        _state.value = _state.value.copy(showEditDialog = true, editingSetting = setting, editValue = setting.value ?: "")
    }
    fun dismissEditDialog() { _state.value = _state.value.copy(showEditDialog = false, editingSetting = null) }
    fun onEditValueChange(v: String) { _state.value = _state.value.copy(editValue = v) }

    fun saveSetting() {
        val s = _state.value.editingSetting ?: return
        viewModelScope.launch {
            settingsRepo.updateSetting(s.id ?: 0, _state.value.editValue).onSuccess {
                dismissEditDialog(); loadAll()
                _state.value = _state.value.copy(successMessage = "Cập nhật thành công")
            }
        }
    }

    fun showNotificationForm() { _state.value = _state.value.copy(showNotificationForm = true, notifTitle = "", notifMessage = "", notifUserId = "") }
    fun dismissNotificationForm() { _state.value = _state.value.copy(showNotificationForm = false) }
    fun onNotifField(f: String, v: String) { _state.value = when (f) { "title" -> _state.value.copy(notifTitle = v); "message" -> _state.value.copy(notifMessage = v); "userId" -> _state.value.copy(notifUserId = v); else -> _state.value } }

    fun sendNotification() {
        viewModelScope.launch {
            settingsRepo.sendNotification(SendNotificationRequest(
                userId = _state.value.notifUserId.toIntOrNull(), title = _state.value.notifTitle, message = _state.value.notifMessage
            )).onSuccess { dismissNotificationForm(); _state.value = _state.value.copy(successMessage = "Gửi thông báo thành công") }
        }
    }

    fun showEmailForm() { _state.value = _state.value.copy(showEmailForm = true, emailRecipients = "", emailSubject = "", emailBody = "") }
    fun dismissEmailForm() { _state.value = _state.value.copy(showEmailForm = false) }
    fun onEmailField(f: String, v: String) { _state.value = when (f) { "recipients" -> _state.value.copy(emailRecipients = v); "subject" -> _state.value.copy(emailSubject = v); "body" -> _state.value.copy(emailBody = v); else -> _state.value } }

    fun sendEmail() {
        viewModelScope.launch {
            settingsRepo.sendEmail(SendEmailRequest(
                recipients = _state.value.emailRecipients.split(",").map { it.trim() },
                subject = _state.value.emailSubject, body = _state.value.emailBody
            )).onSuccess { dismissEmailForm(); _state.value = _state.value.copy(successMessage = "Gửi email thành công") }
        }
    }

    fun clearSuccess() { _state.value = _state.value.copy(successMessage = null) }
}
