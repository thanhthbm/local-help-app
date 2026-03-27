package com.localhelp.app.data.remote

import com.localhelp.app.model.response.ApiResponse
import com.localhelp.app.model.response.ConversationResponse
import retrofit2.Response
import retrofit2.http.GET

interface ConversationService {
    @GET("/api/conversations")
    suspend fun getMyConversations(): Response<ApiResponse<List<ConversationResponse>>>
}