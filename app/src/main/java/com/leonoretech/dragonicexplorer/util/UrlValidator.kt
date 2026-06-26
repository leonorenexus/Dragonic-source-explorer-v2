package com.leonoretech.dragonicexplorer.util

import java.net.URI

/** Validates that a user-supplied string is a well-formed, public http(s) URL. */
object UrlValidator {

    fun isValid(input: String): Boolean {
        if (input.isBlank()) return false
        return try {
            val uri = URI(input.trim())
            val scheme = uri.scheme?.lowercase()
            val host = uri.host
            (scheme == "http" || scheme == "https") && !host.isNullOrBlank() && host.contains(".")
        } catch (e: Exception) {
            false
        }
    }

    /** Adds an https:// scheme if the user typed a bare domain. */
    fun normalize(input: String): String {
        val trimmed = input.trim()
        return if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            "https://$trimmed"
        } else trimmed
    }
}
