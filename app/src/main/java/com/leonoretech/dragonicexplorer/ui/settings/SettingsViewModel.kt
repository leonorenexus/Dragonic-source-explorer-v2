package com.leonoretech.dragonicexplorer.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leonoretech.dragonicexplorer.data.local.SecureTokenStore
import com.leonoretech.dragonicexplorer.data.local.SettingsDataStore
import com.leonoretech.dragonicexplorer.worker.WorkScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class SettingsUiState(
    val repoOwner: String = "",
    val repoName: String = "",
    val workflowFile: String = "website-analysis.yml",
    val defaultBranch: String = "main",
    val autoMonitorMinutes: Int = 30,
    val notificationsEnabled: Boolean = true,
    val hasToken: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore,
    private val secureTokenStore: SecureTokenStore,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState(hasToken = secureTokenStore.getToken() != null))
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        viewModelScope.launch { settingsDataStore.repoOwner.collect { v -> _uiState.value = _uiState.value.copy(repoOwner = v) } }
        viewModelScope.launch { settingsDataStore.repoName.collect { v -> _uiState.value = _uiState.value.copy(repoName = v) } }
        viewModelScope.launch { settingsDataStore.workflowFile.collect { v -> _uiState.value = _uiState.value.copy(workflowFile = v) } }
        viewModelScope.launch { settingsDataStore.defaultBranch.collect { v -> _uiState.value = _uiState.value.copy(defaultBranch = v) } }
        viewModelScope.launch {
            settingsDataStore.autoMonitorMinutes.collect { v ->
                _uiState.value = _uiState.value.copy(autoMonitorMinutes = v)
                if (_uiState.value.notificationsEnabled) WorkScheduler.schedulePeriodicStatusCheck(context, v)
            }
        }
        viewModelScope.launch {
            settingsDataStore.notificationsEnabled.collect { v ->
                _uiState.value = _uiState.value.copy(notificationsEnabled = v)
                if (v) {
                    WorkScheduler.schedulePeriodicStatusCheck(context, _uiState.value.autoMonitorMinutes)
                } else {
                    WorkScheduler.cancelPeriodicStatusCheck(context)
                }
            }
        }
    }

    fun updateRepoOwner(value: String) = viewModelScope.launch { settingsDataStore.setRepoOwner(value) }
    fun updateRepoName(value: String) = viewModelScope.launch { settingsDataStore.setRepoName(value) }
    fun updateWorkflowFile(value: String) = viewModelScope.launch { settingsDataStore.setWorkflowFile(value) }
    fun updateBranch(value: String) = viewModelScope.launch { settingsDataStore.setDefaultBranch(value) }
    fun updateAutoMonitorMinutes(value: Int) = viewModelScope.launch { settingsDataStore.setAutoMonitorMinutes(value) }
    fun updateNotificationsEnabled(value: Boolean) = viewModelScope.launch { settingsDataStore.setNotificationsEnabled(value) }

    fun saveToken(token: String) {
        secureTokenStore.saveToken(token)
        _uiState.value = _uiState.value.copy(hasToken = token.isNotBlank())
    }

    fun clearToken() {
        secureTokenStore.clearToken()
        _uiState.value = _uiState.value.copy(hasToken = false)
    }
}
