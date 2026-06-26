package com.leonoretech.dragonicexplorer.util

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

/** Formats GitHub's ISO-8601 timestamps into the casual relative-time style used in the UI. */
object DateUtils {

    private fun parseIso(value: String?): Long? {
        if (value.isNullOrBlank()) return null
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            sdf.parse(value)?.time
        } catch (e: Exception) {
            null
        }
    }

    fun relativeTime(value: String?): String {
        val millis = parseIso(value) ?: return "-"
        val diffMinutes = (System.currentTimeMillis() - millis) / 60000
        return when {
            diffMinutes < 1 -> "baru saja"
            diffMinutes < 60 -> "$diffMinutes menit yang lalu"
            diffMinutes < 1440 -> "${diffMinutes / 60} jam yang lalu"
            else -> "${diffMinutes / 1440} hari yang lalu"
        }
    }

    fun durationLabel(start: String?, end: String?): String {
        val startMs = parseIso(start) ?: return "--:--"
        val endMs = parseIso(end) ?: System.currentTimeMillis()
        val seconds = ((endMs - startMs) / 1000).coerceAtLeast(0)
        return String.format("%02d:%02d", seconds / 60, seconds % 60)
    }

    fun formatBytes(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB")
        var value = bytes.toDouble()
        var unitIndex = 0
        while (value >= 1024 && unitIndex < units.size - 1) {
            value /= 1024
            unitIndex++
        }
        return String.format("%.1f %s", value, units[unitIndex])
    }
}
