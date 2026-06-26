package com.leonoretech.dragonicexplorer.download

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import javax.inject.Inject
import javax.inject.Singleton

/** Hands artifact ZIP URLs off to the system Download Manager for reliable background transfer. */
@Singleton
class DownloadManagerHelper @Inject constructor() {

    fun enqueueDownload(context: Context, url: String, fileName: String): Long {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle(fileName)
            .setDescription("Dragonic Source Explorer artifact")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        return downloadManager.enqueue(request)
    }
}
