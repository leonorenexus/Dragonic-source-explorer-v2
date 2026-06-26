
package com.leonoretech.dragonicexplorer.data.remote

import com.leonoretech.dragonicexplorer.data.local.SecureTokenStore
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * Attaches the user's GitHub Personal Access Token only to requests targeting
 * api.github.com. OkHttp automatically strips the Authorization header when a
 * redirect crosses to a different host (e.g. GitHub's artifact/log storage),
 * so the token is never leaked to third-party storage URLs.
 */
class AuthInterceptor @Inject constructor(
    private val secureTokenStore: SecureTokenStore
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val token = secureTokenStore.getToken()

        val requestBuilder = original.newBuilder()
            .addHeader("Accept", "application/vnd.github+json")
            .addHeader("X-GitHub-Api-Version", "2022-11-28")

        if (!token.isNullOrBlank() && original.url.host == "api.github.com") {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}
