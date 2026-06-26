package com.leonoretech.dragonicexplorer.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.leonoretech.dragonicexplorer.data.model.ScanHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanHistoryDao {

    @Insert
    suspend fun insert(entity: ScanHistoryEntity): Long

    @Update
    suspend fun update(entity: ScanHistoryEntity)

    @Query("SELECT * FROM scan_history ORDER BY triggeredAt DESC")
    fun observeAll(): Flow<List<ScanHistoryEntity>>

    @Query("SELECT * FROM scan_history WHERE runId = :runId LIMIT 1")
    suspend fun findByRunId(runId: Long): ScanHistoryEntity?

    @Query("DELETE FROM scan_history")
    suspend fun clearAll()
}
