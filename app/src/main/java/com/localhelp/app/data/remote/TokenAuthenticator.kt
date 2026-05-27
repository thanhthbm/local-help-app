package com.localhelp.app.data.remote

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.localhelp.app.data.local.TokenManager
import com.localhelp.app.data.local.UserManager
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val userManager: UserManager,
    private val tokenManager: TokenManager
): Authenticator {
    /**
     * Xử lý response 401 từ backend bằng cách ép Firebase cấp token mới.
     *
     * Nếu refresh thất bại hoặc không còn Firebase user, app đăng xuất local
     * để tránh giữ phiên không hợp lệ.
     */
    override fun authenticate(route: Route?, response: Response): Request? {
        val responseCount = response.responseCount()
        Log.d("TokenAuthenticator", "authenticate called. Count: $responseCount, URL: ${response.request.url}")

        // Avoid infinite loops by limiting retries. 
        // OkHttp counts the first 401 as 1. The first retry will be 2.
        if (responseCount >= 2) {
            Log.d("TokenAuthenticator", "Too many authentication attempts ($responseCount), stopping retry.")
            return null
        }

        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser == null){
            Log.d("TokenAuthenticator", "No current Firebase user, logging out.")
            userManager.logout()
            return null
        }

        return try {
            Log.d("TokenAuthenticator", "Refreshing token...")
            val tokenResult = runBlocking {
                currentUser.getIdToken(true).await()
            }

            val newToken = tokenResult.token

            if (newToken != null){
                Log.d("TokenAuthenticator", "New token obtained.")
                tokenManager.saveToken(newToken)
                
                // Update session if we have a user
                userManager.currentUser.value?.let { user ->
                    userManager.saveSession(user, newToken)
                }

                response.request.newBuilder()
                    .header("Authorization", "Bearer $newToken")
                    .build()

            } else {
                Log.d("TokenAuthenticator", "Failed to get token string.")
                userManager.logout()
                null
            }
        } catch (e: Exception){
            Log.e("TokenAuthenticator", "Error during token refresh", e)
            userManager.logout()
            null
        }
    }

    /** Đếm số lần OkHttp đã retry request này để tránh vòng lặp refresh token vô hạn. */
    private fun Response.responseCount(): Int {
        var result = 1
        var prior = priorResponse
        while (prior != null) {
            result++
            prior = prior.priorResponse
        }
        return result
    }
}
