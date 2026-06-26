package com.leonoretech.dragonicexplorer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.leonoretech.dragonicexplorer.data.model.ScanHistoryEntity

@Database(entities = [ScanHistoryEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scanHistoryDao(): ScanHistoryDao
}
