package com.localhelp.app.data.remote

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
    override fun authenticate(route: Route?, response: Response): Request? {
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser == null){
            userManager.logout()
            return null
        }

        return try{
            val tokenResult = runBlocking {
                currentUser.getIdToken(true).await()
            }

            val newToken = tokenResult.token

            if (newToken != null){
                tokenManager.saveToken(newToken)
                val currentUserState = userManager.currentUser.value
                if (currentUserState != null) {
                    userManager.saveSession(currentUserState, newToken)
                }

                response.request.newBuilder()
                    .header("Authorization", "Bearer $newToken")
                    .build()

            } else {
                userManager.logout()
                null
            }
        } catch (e: Exception){
            userManager.logout()
            null
        }
    }
}