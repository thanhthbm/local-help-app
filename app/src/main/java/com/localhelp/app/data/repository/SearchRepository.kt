package com.localhelp.app.data.repository

import com.localhelp.app.data.local.SearchHistoryDataStore
import com.localhelp.app.data.remote.JobService
import com.localhelp.app.model.request.SearchJobRequest
import com.localhelp.app.model.response.JobResponse
import com.localhelp.app.model.response.Meta
import com.localhelp.app.model.response.ResultPaginationDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import kotlin.collections.emptyList

class SearchRepository @Inject constructor(
    private val jobService: JobService,
    private val historyDataStore: SearchHistoryDataStore
){
    val searchHistory: Flow<List<String>> = historyDataStore.historyFlow

    suspend fun addSearchHistory(query: String) {
        historyDataStore.addHistory(query)
    }

    suspend fun removeSearchHistory(query: String) {
        historyDataStore.removeHistory(query)
    }

    suspend fun clearAllSearchHistory() {
        historyDataStore.clearAll()
    }

    fun searchJobs(request: SearchJobRequest): Flow<Result<ResultPaginationDTO<List<JobResponse>>>> = flow {
        val response = jobService.searchJobs(request)

        if (response.isSuccessful) {
            val apiResponse = response.body()

            if (apiResponse != null && apiResponse.statusCode == 200) {
                val paginationData = apiResponse.data

                if (paginationData != null) {
                    emit(Result.success(paginationData))
                } else {
                    val emptyMeta = Meta(page = 0, size = 10, pages = 0, total = 0L)
                    val emptyList : List<JobResponse> = listOf()
                    val emptyPagination = ResultPaginationDTO(meta = emptyMeta, result = emptyList)
                    emit(Result.success(emptyPagination))
                }

            } else {
                val errorMessage = apiResponse?.error
                    ?: apiResponse?.message?.toString()
                    ?: "Lỗi từ server: Mã ${apiResponse?.statusCode}"
                emit(Result.failure(Exception(errorMessage)))
            }
        } else {
            val errorMsg = response.errorBody()?.string() ?: "Lỗi HTTP: ${response.code()}"
            emit(Result.failure(Exception(errorMsg)))
        }

    }.catch { e ->
        emit(Result.failure(Exception("Lỗi kết nối: ${e.localizedMessage}")))
    }.flowOn(Dispatchers.IO)
}