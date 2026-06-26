package com.leonoretech.dragonicexplorer.data.model

import com.google.gson.annotations.SerializedName

data class Artifact(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("size_in_bytes") val sizeInBytes: Long,
    @SerializedName("archive_download_url") val archiveDownloadUrl: String,
    @SerializedName("expired") val expired: Boolean,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("expires_at") val expiresAt: String?
)

data class ArtifactsResponse(
    @SerializedName("total_count") val totalCount: Int,
    @SerializedName("artifacts") val artifacts: List<Artifact>
)
