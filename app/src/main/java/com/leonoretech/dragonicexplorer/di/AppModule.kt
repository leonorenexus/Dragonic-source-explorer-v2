package com.leonoretech.dragonicexplorer.di

import android.content.Context
import androidx.room.Room
import com.leonoretech.dragonicexplorer.data.local.AppDatabase
import com.leonoretech.dragonicexplorer.data.local.ScanHistoryDao
import com.leonoretech.dragonicexplorer.data.repository.GitHubRepository
import com.leonoretech.dragonicexplorer.data.repository.GitHubRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindGitHubRepository(impl: GitHubRepositoryImpl): GitHubRepository
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "dragonic_db").build()

    @Provides
    fun provideScanHistoryDao(db: AppDatabase): ScanHistoryDao = db.scanHistoryDao()
}
