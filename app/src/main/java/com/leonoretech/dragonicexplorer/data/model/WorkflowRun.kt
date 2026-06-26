package com.leonoretech.dragonicexplorer.data.model

import com.google.gson.annotations.SerializedName

/**
 * Represents a single GitHub Actions workflow run, returned by the
 * /repos/{owner}/{repo}/actions/runs endpoint.
 */
data class WorkflowRun(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String?,
    @SerializedName("head_branch") val headBranch: String?,
    @SerializedName("run_number") val runNumber: Int,
    @SerializedName("status") val status: String,         // queued, in_progress, completed
    @SerializedName("conclusion") val conclusion: String?, // success, failure, cancelled, null
    @SerializedName("html_url") val htmlUrl: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("run_started_at") val runStartedAt: String?
)

data class WorkflowRunsResponse(
    @SerializedName("total_count") val totalCount: Int,
    @SerializedName("workflow_runs") val workflowRuns: List<WorkflowRun>
)
