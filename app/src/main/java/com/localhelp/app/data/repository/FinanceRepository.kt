package com.localhelp.app.data.repository

import com.localhelp.app.data.remote.FinanceService
import com.localhelp.app.model.response.CategoryDetailResponse
import com.localhelp.app.model.response.FinanceOverviewResponse
import javax.inject.Inject

class FinanceRepository @Inject constructor(
    private val financeService: FinanceService
) {
    suspend fun getFinanceOverview(type: String, month: Int, year: Int): Result<FinanceOverviewResponse> {
        return try {
            val response = financeService.getFinanceOverview(type, month, year)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception("Lỗi máy chủ: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCategoryDetails(categoryId: Long, type: String, month: Int, year: Int): Result<CategoryDetailResponse> {
        return try {
            val response = financeService.getCategoryDetails(categoryId, type, month, year)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception("Lỗi máy chủ: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
