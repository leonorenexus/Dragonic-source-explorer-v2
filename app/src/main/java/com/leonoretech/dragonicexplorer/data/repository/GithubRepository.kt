package com.leonoretech.dragonicexplorer.data.repository

import com.leonoretech.dragonicexplorer.data.model.Artifact
import com.leonoretech.dragonicexplorer.data.model.Job
import com.leonoretech.dragonicexplorer.data.model.ScanHistoryEntity
import com.leonoretech.dragonicexplorer.data.model.WorkflowRun
import com.leonoretech.dragonicexplorer.util.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Single source of truth for all GitHub Actions interactions (Repository Pattern).
 * ViewModels depend on this interface only, never on Retrofit/OkHttp directly.
 */
interface GitHubRepository {
    suspend fun dispatchWorkflow(targetUrl: String): Resource<Unit>
    suspend fun fetchWorkflowRuns(): Resource<List<WorkflowRun>>
    suspend fun fetchWorkflowRun(runId: Long): Resource<WorkflowRun>
    suspend fun fetchJobs(runId: Long): Resource<List<Job>>
    suspend fun fetchJobLogs(jobId: Long): Resource<String>
    suspend fun fetchArtifacts(runId: Long): Resource<List<Artifact>>
    suspend fun resolveArtifactDownloadUrl(artifactId: Long): Resource<String>
    fun observeScanHistory(): Flow<List<ScanHistoryEntity>>
    suspend fun recordScan(url: String, runId: Long?, status: String)
}
