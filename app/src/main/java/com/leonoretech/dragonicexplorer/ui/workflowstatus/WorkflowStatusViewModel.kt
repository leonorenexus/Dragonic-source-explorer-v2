package com.leonoretech.dragonicexplorer.ui.workflowstatus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leonoretech.dragonicexplorer.data.model.WorkflowRun
import com.leonoretech.dragonicexplorer.data.repository.GitHubRepository
import com.leonoretech.dragonicexplorer.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WorkflowStatusUiState(
    val isLoading: Boolean = false,
    val runs: List<WorkflowRun> = emptyList(),
    val errorMessage: String? = null
) {
    val total get() = runs.size
    val success get() = runs.count { it.conclusion == "success" }
    val failed get() = runs.count { it.conclusion == "failure" }
    val running get() = runs.count { it.status != "completed" }
}

@HiltViewModel
class WorkflowStatusViewModel @Inject constructor(
    private val repository: GitHubRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkflowStatusUiState())
    val uiState: StateFlow<WorkflowStatusUiState> = _uiState

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when (val result = repository.fetchWorkflowRuns()) {
                is Resource.Success -> _uiState.value = _uiState.value.copy(isLoading = false, runs = result.data)
                is Resource.Error -> _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = result.message)
                Resource.Loading -> Unit
            }
        }
    }
}
