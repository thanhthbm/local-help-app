package com.localhelp.app.data.remote

import com.localhelp.app.model.response.ApiResponse
import com.localhelp.app.model.response.ConversationResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ConversationService {
    @GET("/api/conversations")
    suspend fun getMyConversations(): Response<ApiResponse<List<ConversationResponse>>>

    @POST("/api/conversations/start")
    suspend fun startConversation(@Query("targetUserId") targetUserId: String): Response<ApiResponse<ConversationResponse>>
}