package com.leonoretech.dragonicexplorer.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.leonoretech.dragonicexplorer.data.repository.GitHubRepository
import com.leonoretech.dragonicexplorer.notification.NotificationHelper
import com.leonoretech.dragonicexplorer.util.Resource
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Background status check: periodically polls the most recent workflow run and
 * fires a notification once it leaves the "in_progress"/"queued" state.
 */
@HiltWorker
class WorkflowStatusWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: GitHubRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val runsResult = repository.fetchWorkflowRuns()
            if (runsResult is Resource.Success) {
                val latest = runsResult.data.firstOrNull()
                if (latest != null && latest.status == "completed") {
                    NotificationHelper.showWorkflowCompletedNotification(
                        applicationContext,
                        latest.runNumber,
                        latest.conclusion ?: "unknown"
                    )
                }
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "workflow_status_check"
    }
}
