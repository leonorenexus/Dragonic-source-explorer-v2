package com.leonoretech.dragonicexplorer.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "dragonic_settings")

/** Non-sensitive app/repository configuration, persisted with Jetpack DataStore. */
@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val REPO_OWNER = stringPreferencesKey("repo_owner")
        val REPO_NAME = stringPreferencesKey("repo_name")
        val WORKFLOW_FILE = stringPreferencesKey("workflow_file")
        val DEFAULT_BRANCH = stringPreferencesKey("default_branch")
        val AUTO_MONITOR_MINUTES = intPreferencesKey("auto_monitor_minutes")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    }

    val repoOwner: Flow<String> = context.dataStore.data.map { it[Keys.REPO_OWNER] ?: "" }
    val repoName: Flow<String> = context.dataStore.data.map { it[Keys.REPO_NAME] ?: "" }
    val workflowFile: Flow<String> =
        context.dataStore.data.map { it[Keys.WORKFLOW_FILE] ?: "website-analysis.yml" }
    val defaultBranch: Flow<String> = context.dataStore.data.map { it[Keys.DEFAULT_BRANCH] ?: "main" }
    val autoMonitorMinutes: Flow<Int> =
        context.dataStore.data.map { it[Keys.AUTO_MONITOR_MINUTES] ?: 30 }
    val notificationsEnabled: Flow<Boolean> =
        context.dataStore.data.map { it[Keys.NOTIFICATIONS_ENABLED] ?: true }

    suspend fun setRepoOwner(value: String) = context.dataStore.edit { it[Keys.REPO_OWNER] = value }
    suspend fun setRepoName(value: String) = context.dataStore.edit { it[Keys.REPO_NAME] = value }
    suspend fun setWorkflowFile(value: String) = context.dataStore.edit { it[Keys.WORKFLOW_FILE] = value }
    suspend fun setDefaultBranch(value: String) = context.dataStore.edit { it[Keys.DEFAULT_BRANCH] = value }
    suspend fun setAutoMonitorMinutes(value: Int) =
        context.dataStore.edit { it[Keys.AUTO_MONITOR_MINUTES] = value }
    suspend fun setNotificationsEnabled(value: Boolean) =
        context.dataStore.edit { it[Keys.NOTIFICATIONS_ENABLED] = value }
}
