package com.leonoretech.dragonicexplorer.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leonoretech.dragonicexplorer.data.model.ScanHistoryEntity
import com.leonoretech.dragonicexplorer.data.repository.GitHubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    repository: GitHubRepository
) : ViewModel() {

    /** Local (Room-backed) history of every scan triggered from this device. */
    val history: StateFlow<List<ScanHistoryEntity>> = repository.observeScanHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
