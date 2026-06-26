package com.leonoretech.dragonicexplorer.data.remote

import com.leonoretech.dragonicexplorer.data.model.ArtifactsResponse
import com.leonoretech.dragonicexplorer.data.model.JobsResponse
import com.leonoretech.dragonicexplorer.data.model.WorkflowDispatchRequest
import com.leonoretech.dragonicexplorer.data.model.WorkflowRun
import com.leonoretech.dragonicexplorer.data.model.WorkflowRunsResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * GitHub REST API surface used by Dragonic Source Explorer.
 * Base URL: https://api.github.com/
 *
 * Only endpoints required to dispatch and monitor a workflow_dispatch-triggered
 * Actions workflow, and to list/download its artifacts and logs, are exposed here.
 */
interface GitHubApiService {

    /** Triggers the workflow via workflow_dispatch. workflowFile e.g. "website-analysis.yml" */
    @POST("repos/{owner}/{repo}/actions/workflows/{workflowFile}/dispatches")
    suspend fun dispatchWorkflow(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("workflowFile") workflowFile: String,
        @Body request: WorkflowDispatchRequest
    ): Response<Unit>

    @GET("repos/{owner}/{repo}/actions/workflows/{workflowFile}/runs")
    suspend fun getWorkflowRuns(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("workflowFile") workflowFile: String,
        @Query("per_page") perPage: Int = 25
    ): Response<WorkflowRunsResponse>

    @GET("repos/{owner}/{repo}/actions/runs/{runId}")
    suspend fun getWorkflowRun(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("runId") runId: Long
    ): Response<WorkflowRun>

    @GET("repos/{owner}/{repo}/actions/runs/{runId}/jobs")
    suspend fun getWorkflowRunJobs(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("runId") runId: Long
    ): Response<JobsResponse>

    /** GitHub redirects (302) this request to a pre-signed plain-text log URL; OkHttp follows it. */
    @GET("repos/{owner}/{repo}/actions/jobs/{jobId}/logs")
    suspend fun getJobLogs(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("jobId") jobId: Long
    ): Response<ResponseBody>

    @GET("repos/{owner}/{repo}/actions/runs/{runId}/artifacts")
    suspend fun getRunArtifacts(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("runId") runId: Long
    ): Response<ArtifactsResponse>
}
