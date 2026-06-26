package com.leonoretech.dragonicexplorer

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.leonoretech.dragonicexplorer.notification.NotificationHelper
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Application entry point. Annotated for Hilt dependency injection and
 * configured as a WorkManager Configuration.Provider so that background
 * WorkflowStatusWorker instances can have dependencies injected into them.
 */
@HiltAndroidApp
class DragonicApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannels(this)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
