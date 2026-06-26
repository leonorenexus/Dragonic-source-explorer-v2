package com.leonoretech.dragonicexplorer.ui.downloads

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leonoretech.dragonicexplorer.data.model.Artifact
import com.leonoretech.dragonicexplorer.data.repository.GitHubRepository
import com.leonoretech.dragonicexplorer.download.DownloadManagerHelper
import com.leonoretech.dragonicexplorer.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DownloadsUiState(
    val isLoading: Boolean = false,
    val artifacts: List<Artifact> = emptyList(),
    val totalSizeBytes: Long = 0,
    val errorMessage: String? = null,
    val resolvingArtifactId: Long? = null
)

@HiltViewModel
class DownloadsViewModel @Inject constructor(
    private val repository: GitHubRepository,
    private val downloadManagerHelper: DownloadManagerHelper,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(DownloadsUiState())
    val uiState: StateFlow<DownloadsUiState> = _uiState

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when (val runsResult = repository.fetchWorkflowRuns()) {
                is Resource.Success -> {
                    val allArtifacts = mutableListOf<Artifact>()
                    for (run in runsResult.data.take(10)) {
                        val artifactsResult = repository.fetchArtifacts(run.id)
                        if (artifactsResult is Resource.Success) {
                            allArtifacts.addAll(artifactsResult.data)
                        }
                    }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        artifacts = allArtifacts,
                        totalSizeBytes = allArtifacts.sumOf { it.sizeInBytes }
                    )
                }
                is Resource.Error -> _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = runsResult.message)
                Resource.Loading -> Unit
            }
        }
    }

    fun downloadArtifact(artifact: Artifact) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(resolvingArtifactId = artifact.id)
            val result = repository.resolveArtifactDownloadUrl(artifact.id)
            if (result is Resource.Success) {
                downloadManagerHelper.enqueueDownload(context, result.data, "${artifact.name}.zip")
            } else if (result is Resource.Error) {
                _uiState.value = _uiState.value.copy(errorMessage = result.message)
            }
            _uiState.value = _uiState.value.copy(resolvingArtifactId = null)
        }
    }
}
