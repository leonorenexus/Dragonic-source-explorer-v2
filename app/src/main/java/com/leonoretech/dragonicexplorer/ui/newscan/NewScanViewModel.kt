package com.leonoretech.dragonicexplorer.ui.newscan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leonoretech.dragonicexplorer.data.repository.GitHubRepository
import com.leonoretech.dragonicexplorer.util.Resource
import com.leonoretech.dragonicexplorer.util.UrlValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ScanDispatchState {
    object Idle : ScanDispatchState()
    object Dispatching : ScanDispatchState()
    object Success : ScanDispatchState()
    data class Error(val message: String) : ScanDispatchState()
}

data class NewScanUiState(
    val url: String = "",
    val urlError: String? = null,
    val dispatchState: ScanDispatchState = ScanDispatchState.Idle
)

@HiltViewModel
class NewScanViewModel @Inject constructor(
    private val repository: GitHubRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewScanUiState())
    val uiState: StateFlow<NewScanUiState> = _uiState

    fun onUrlChanged(value: String) {
        _uiState.value = _uiState.value.copy(url = value, urlError = null)
    }

    /** Validates the URL is a well-formed public http(s) address, then triggers workflow_dispatch. */
    fun submitScan() {
        val normalized = UrlValidator.normalize(_uiState.value.url)
        if (!UrlValidator.isValid(normalized)) {
            _uiState.value = _uiState.value.copy(urlError = "Masukkan URL website publik yang valid")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(dispatchState = ScanDispatchState.Dispatching)
            when (val result = repository.dispatchWorkflow(normalized)) {
                is Resource.Success -> _uiState.value = _uiState.value.copy(dispatchState = ScanDispatchState.Success)
                is Resource.Error -> _uiState.value = _uiState.value.copy(dispatchState = ScanDispatchState.Error(result.message))
                Resource.Loading -> Unit
            }
        }
    }
}
