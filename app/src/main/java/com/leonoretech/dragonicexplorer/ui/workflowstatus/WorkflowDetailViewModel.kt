package com.leonoretech.dragonicexplorer.ui.workflowstatus

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leonoretech.dragonicexplorer.data.model.Artifact
import com.leonoretech.dragonicexplorer.data.model.Job
import com.leonoretech.dragonicexplorer.data.model.WorkflowRun
import com.leonoretech.dragonicexplorer.data.repository.GitHubRepository
import com.leonoretech.dragonicexplorer.download.DownloadManagerHelper
import com.leonoretech.dragonicexplorer.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WorkflowDetailUiState(
    val isLoading: Boolean = false,
    val run: WorkflowRun? = null,
    val jobs: List<Job> = emptyList(),
    val artifacts: List<Artifact> = emptyList(),
    val logsText: String? = null,
    val isLoadingLogs: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class WorkflowDetailViewModel @Inject constructor(
    private val repository: GitHubRepository,
    private val downloadManagerHelper: DownloadManagerHelper,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkflowDetailUiState())
    val uiState: StateFlow<WorkflowDetailUiState> = _uiState

    fun load(runId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val runResult = repository.fetchWorkflowRun(runId)
            val jobsResult = repository.fetchJobs(runId)
            val artifactsResult = repository.fetchArtifacts(runId)

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                run = (runResult as? Resource.Success)?.data,
                jobs = (jobsResult as? Resource.Success)?.data.orEmpty(),
                artifacts = (artifactsResult as? Resource.Success)?.data.orEmpty(),
                errorMessage = (runResult as? Resource.Error)?.message
            )
        }
    }

    fun loadLogs(jobId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingLogs = true)
            _uiState.value = when (val result = repository.fetchJobLogs(jobId)) {
                is Resource.Success -> _uiState.value.copy(isLoadingLogs = false, logsText = result.data)
                is Resource.Error -> _uiState.value.copy(isLoadingLogs = false, logsText = "Error: ${result.message}")
                Resource.Loading -> _uiState.value
            }
        }
    }

    fun dismissLogs() {
        _uiState.value = _uiState.value.copy(logsText = null)
    }

    fun downloadArtifact(artifact: Artifact) {
        viewModelScope.launch {
            val result = repository.resolveArtifactDownloadUrl(artifact.id)
            if (result is Resource.Success) {
                downloadManagerHelper.enqueueDownload(context, result.data, "${artifact.name}.zip")
            } else if (result is Resource.Error) {
                _uiState.value = _uiState.value.copy(errorMessage = result.message)
            }
        }
    }
}
