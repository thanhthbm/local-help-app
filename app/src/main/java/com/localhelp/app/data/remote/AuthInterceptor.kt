package com.localhelp.app.data.remote

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.localhelp.app.data.local.TokenManager
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * Interceptor to automatically attach the Firebase ID Token to outgoing requests.
 * It proactively fetches a valid token from Firebase before each request.
 */
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // If the request already has an Authorization header (e.g., from @Header in AuthService), do not overwrite it.
        if (originalRequest.header("Authorization") != null) {
            return chain.proceed(originalRequest)
        }

        // Proactively ensure we have a valid, unexpired token from Firebase
        val currentUser = FirebaseAuth.getInstance().currentUser
        val token = if (currentUser != null) {
            runBlocking {
                try {
                    // getIdToken(false) returns the cached token if it hasn't expired, 
                    // otherwise it fetches a fresh one.
                    val result = currentUser.getIdToken(false).await()
                    val t = result.token
                    if (t != null) {
                        tokenManager.saveToken(t)
                        t
                    } else {
                        tokenManager.getToken()
                    }
                } catch (e: Exception) {
                    Log.e("AuthInterceptor", "Failed to get Firebase token proactively", e)
                    // Fallback to whatever we have in TokenManager
                    tokenManager.getToken()
                }
            }
        } else {
            tokenManager.getToken()
        }

        val requestBuilder = originalRequest.newBuilder()

        if (!token.isNullOrEmpty()) {
            requestBuilder.header("Authorization", "Bearer $token")
            Log.d("AuthInterceptor", "Token attached to ${originalRequest.url}")
        } else {
            Log.w("AuthInterceptor", "No token available for authenticated endpoint: ${originalRequest.url}")
        }

        return chain.proceed(requestBuilder.build())
    }
}
