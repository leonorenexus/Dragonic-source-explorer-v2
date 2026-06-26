package com.leonoretech.dragonicexplorer.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/** Schedules/cancels the periodic background workflow status check. */
object WorkScheduler {

    fun schedulePeriodicStatusCheck(context: Context, intervalMinutes: Int) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // WorkManager enforces a 15 minute minimum for periodic work.
        val request = PeriodicWorkRequestBuilder<WorkflowStatusWorker>(
            intervalMinutes.toLong().coerceAtLeast(15), TimeUnit.MINUTES
        ).setConstraints(constraints).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WorkflowStatusWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    fun cancelPeriodicStatusCheck(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WorkflowStatusWorker.WORK_NAME)
    }
}
