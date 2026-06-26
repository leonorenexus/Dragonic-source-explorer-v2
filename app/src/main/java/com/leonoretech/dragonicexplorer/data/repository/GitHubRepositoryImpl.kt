package com.leonoretech.dragonicexplorer.data.repository

import com.leonoretech.dragonicexplorer.data.local.ScanHistoryDao
import com.leonoretech.dragonicexplorer.data.local.SettingsDataStore
import com.leonoretech.dragonicexplorer.data.model.Artifact
import com.leonoretech.dragonicexplorer.data.model.Job
import com.leonoretech.dragonicexplorer.data.model.ScanHistoryEntity
import com.leonoretech.dragonicexplorer.data.model.WorkflowDispatchRequest
import com.leonoretech.dragonicexplorer.data.model.WorkflowRun
import com.leonoretech.dragonicexplorer.data.remote.GitHubApiService
import com.leonoretech.dragonicexplorer.di.NoRedirectClient
import com.leonoretech.dragonicexplorer.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GitHubRepositoryImpl @Inject constructor(
    private val api: GitHubApiService,
    private val settingsDataStore: SettingsDataStore,
    private val scanHistoryDao: ScanHistoryDao,
    @NoRedirectClient private val noRedirectClient: OkHttpClient
) : GitHubRepository {

    private suspend fun owner() = settingsDataStore.repoOwner.first()
    private suspend fun repo() = settingsDataStore.repoName.first()
    private suspend fun workflowFile() = settingsDataStore.workflowFile.first()
    private suspend fun branch() = settingsDataStore.defaultBranch.first()

    override suspend fun dispatchWorkflow(targetUrl: String): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = api.dispatchWorkflow(
                owner = owner(),
                repo = repo(),
                workflowFile = workflowFile(),
                request = WorkflowDispatchRequest(
                    ref = branch(),
                    inputs = mapOf("website_url" to targetUrl)
                )
            )
            if (response.isSuccessful) {
                recordScan(targetUrl, null, "queued")
                Resource.Success(Unit)
            } else {
                Resource.Error(parseError(response.errorBody()?.string()), response.code())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error while dispatching workflow")
        }
    }

    override suspend fun fetchWorkflowRuns(): Resource<List<WorkflowRun>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getWorkflowRuns(owner(), repo(), workflowFile())
            if (response.isSuccessful) {
                Resource.Success(response.body()?.workflowRuns.orEmpty())
            } else {
                Resource.Error(parseError(response.errorBody()?.string()), response.code())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error while fetching workflow runs")
        }
    }

    override suspend fun fetchWorkflowRun(runId: Long): Resource<WorkflowRun> = withContext(Dispatchers.IO) {
        try {
            val response = api.getWorkflowRun(owner(), repo(), runId)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                Resource.Success(body)
            } else {
                Resource.Error(parseError(response.errorBody()?.string()), response.code())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error while fetching workflow run")
        }
    }

    override suspend fun fetchJobs(runId: Long): Resource<List<Job>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getWorkflowRunJobs(owner(), repo(), runId)
            if (response.isSuccessful) {
                Resource.Success(response.body()?.jobs.orEmpty())
            } else {
                Resource.Error(parseError(response.errorBody()?.string()), response.code())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error while fetching jobs")
        }
    }

    override suspend fun fetchJobLogs(jobId: Long): Resource<String> = withContext(Dispatchers.IO) {
        try {
            val response = api.getJobLogs(owner(), repo(), jobId)
            if (response.isSuccessful) {
                Resource.Success(response.body()?.string() ?: "")
            } else {
                Resource.Error("Logs unavailable (HTTP ${response.code()})", response.code())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error while fetching logs")
        }
    }

    override suspend fun fetchArtifacts(runId: Long): Resource<List<Artifact>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getRunArtifacts(owner(), repo(), runId)
            if (response.isSuccessful) {
                Resource.Success(response.body()?.artifacts.orEmpty())
            } else {
                Resource.Error(parseError(response.errorBody()?.string()), response.code())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error while fetching artifacts")
        }
    }

    /**
     * GET .../artifacts/{id}/zip returns a 302 redirect to a pre-signed, time-limited
     * download URL. We resolve it with redirects disabled so the real URL can be handed
     * directly to Android's DownloadManager (which performs the actual file transfer).
     */
    override suspend fun resolveArtifactDownloadUrl(artifactId: Long): Resource<String> =
        withContext(Dispatchers.IO) {
            try {
                val url = "https://api.github.com/repos/${owner()}/${repo()}/actions/artifacts/$artifactId/zip"
                val request = Request.Builder().url(url).build()
                noRedirectClient.newCall(request).execute().use { response ->
                    val location = response.header("Location")
                    if (response.code in 300..399 && !location.isNullOrBlank()) {
                        Resource.Success(location)
                    } else {
                        Resource.Error("Could not resolve artifact download link (HTTP ${response.code})")
                    }
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Network error while resolving artifact URL")
            }
        }

    override fun observeScanHistory(): Flow<List<ScanHistoryEntity>> = scanHistoryDao.observeAll()

    override suspend fun recordScan(url: String, runId: Long?, status: String) {
        scanHistoryDao.insert(
            ScanHistoryEntity(
                url = url,
                runId = runId,
                status = status,
                conclusion = null,
                triggeredAt = System.currentTimeMillis()
            )
        )
    }

    private fun parseError(body: String?): String = body?.take(200) ?: "Unknown error occurred"
}
