package com.localhelp.app.data.repository

import com.localhelp.app.data.remote.ConversationService
import com.localhelp.app.model.response.ConversationResponse
import javax.inject.Inject
/**
 * Repository trung gian giữa ViewModel và ConversationService (Retrofit API).
 *
 * Wrap kết quả API trong Result<T> để ViewModel xử lý onSuccess/onFailure
 * mà không cần try-catch trực tiếp.
 *
 * suspend fun: chạy trong coroutine context của ViewModel (viewModelScope).
 *
 */
class ConversationRepository @Inject constructor(
    private val conversationService: ConversationService
) {
    /**
     * Lấy danh sách tất cả hội thoại của user hiện tại.
     *
     * Gọi GET /api/conversations.
     * Trả Result<List<ConversationResponse>>.
     */
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
    /**
     * Tạo hoặc lấy lại conversation với user mục tiêu.
     *
     * Gọi POST /api/conversations/start?targetUserId={id}.
     * Trả Result.success(ConversationResponse) nếu thành công,
     * Result.failure(exception) nếu lỗi mạng hoặc server.
     *
     * @param targetUserId  ID người dùng muốn bắt đầu nhắn tin
     * @return              Result<ConversationResponse>
     */
    suspend fun startConversation(targetUserId: String): Result<ConversationResponse>{
        val response = conversationService.startConversation(targetUserId)
        if (response.isSuccessful && response.body() != null){
            val apiResponse = response.body()!!
            if (apiResponse.data != null){
                return Result.success(apiResponse.data)
            } else {
                return Result.failure(Exception("Lỗi tạo chat (${response.code()})"))
            }
        }else {
            return Result.failure(Exception("Lỗi tạo chat (${response.code()})"))
        }
    }
}