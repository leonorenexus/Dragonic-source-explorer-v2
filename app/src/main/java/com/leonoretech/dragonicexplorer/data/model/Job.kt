package com.leonoretech.dragonicexplorer.data.model

import com.google.gson.annotations.SerializedName

data class Job(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("status") val status: String,
    @SerializedName("conclusion") val conclusion: String?,
    @SerializedName("started_at") val startedAt: String?,
    @SerializedName("completed_at") val completedAt: String?,
    @SerializedName("steps") val steps: List<JobStep>?
)

data class JobStep(
    @SerializedName("name") val name: String,
    @SerializedName("status") val status: String,
    @SerializedName("conclusion") val conclusion: String?,
    @SerializedName("number") val number: Int
)

data class JobsResponse(
    @SerializedName("total_count") val totalCount: Int,
    @SerializedName("jobs") val jobs: List<Job>
)
