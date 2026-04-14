package com.localhelp.app.data.repository

import com.localhelp.app.data.local.SearchHistoryDataStore
import com.localhelp.app.data.remote.JobService
import com.localhelp.app.model.response.JobResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

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

    suspend fun searchJob(keyword: String): Result<List<JobResponse>> {
        return try {
            val response = jobService.searchJobs(keyword, 1, 10)

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.data != null) {
                    Result.success(body.data.result)
                } else {
                    Result.failure(Exception(response.message()))
                }
            } else {
                Result.failure(Exception("Lỗi server: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}