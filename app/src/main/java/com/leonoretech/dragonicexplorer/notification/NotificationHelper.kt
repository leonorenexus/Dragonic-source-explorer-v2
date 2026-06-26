package com.leonoretech.dragonicexplorer.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.leonoretech.dragonicexplorer.R

object NotificationHelper {

    private const val CHANNEL_ID = "dragonic_workflow_channel"
    private const val CHANNEL_NAME = "Workflow Updates"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications about Dragonic Source Explorer scan workflows"
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    fun showWorkflowCompletedNotification(context: Context, runNumber: Int, conclusion: String) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Scan #$runNumber finished")
            .setContentText("Workflow run completed with status: $conclusion")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        NotificationManagerCompat.from(context).apply {
            notify(runNumber, builder.build())
        }
    }
}
