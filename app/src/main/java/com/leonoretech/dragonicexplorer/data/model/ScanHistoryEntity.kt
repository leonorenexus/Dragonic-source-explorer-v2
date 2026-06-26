package com.leonoretech.dragonicexplorer.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Local (on-device) record of every scan the user has triggered, persisted
 * via Room so History works fully offline regardless of GitHub API availability.
 */
@Entity(tableName = "scan_history")
data class ScanHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val url: String,
    val runId: Long?,
    val status: String,
    val conclusion: String?,
    val triggeredAt: Long
)
