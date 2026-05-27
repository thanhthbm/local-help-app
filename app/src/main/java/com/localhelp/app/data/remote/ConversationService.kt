package com.localhelp.app.data.remote

import com.localhelp.app.model.response.ApiResponse
import com.localhelp.app.model.response.ConversationResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
/**
 * Retrofit2 interface định nghĩa các HTTP call đến Conversation API.
 *
 * Mỗi fun là suspend fun để dùng với Kotlin Coroutines.
 * Response được bọc trong ApiResponse<T> – wrapper chung của project.
 *
 * Base URL: được inject qua Hilt NetworkModule (retrofit.baseUrl).
 *
 */
interface ConversationService {
    /**
     * Gọi GET /api/conversations để lấy danh sách hội thoại của user hiện tại.
     *
     * @return  ApiResponse<List<ConversationResponse>>
     */
    @GET("/api/conversations")
    suspend fun getMyConversations(): Response<ApiResponse<List<ConversationResponse>>>
    /**
     * Gọi POST /api/conversations/start để tạo hoặc lấy lại hội thoại.
     *
     * @param targetUserId  @Query param – ID người dùng muốn nhắn tin
     * @return              ApiResponse<ConversationResponse>
     */
    @POST("/api/conversations/start")
    suspend fun startConversation(@Query("targetUserId") targetUserId: String): Response<ApiResponse<ConversationResponse>>
}