package com.localhelp.app.data.repository

import com.localhelp.app.data.remote.ConversationService
import com.localhelp.app.model.response.ConversationResponse
import javax.inject.Inject

class ConversationRepository @Inject constructor(
    private val conversationService: ConversationService
) {
    suspend fun getMyConversations(): Result<List<ConversationResponse>>{
        return try{
            val response = conversationService.getMyConversations()
            if (response.isSuccessful && response.body() != null){
                val apiResponse = response.body()!!
                if (apiResponse.data != null){
                    Result.success(apiResponse.data)
                } else {
                    Result.success(emptyList())
                }
            } else {
                Result.failure(Exception("Lỗi lấy danh sách chat (${response.code()})"))
            }
        } catch (e: Exception){
            Result.failure(e)
        }
    }
}