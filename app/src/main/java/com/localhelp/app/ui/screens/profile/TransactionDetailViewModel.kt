package com.localhelp.app.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localhelp.app.data.remote.JobDetailApiService
import com.localhelp.app.model.response.JobDetailResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TransactionDetailUiState(
    val isLoading: Boolean = false,
    val data: JobDetailResponse? = null,
    val error: String? = null
)

@HiltViewModel
class TransactionDetailViewModel @Inject constructor(
    private val jobDetailApiService: JobDetailApiService
) : ViewModel() {
    private val _uiState = MutableStateFlow(TransactionDetailUiState())
    val uiState: StateFlow<TransactionDetailUiState> = _uiState

    fun fetchTransactionDetail(jobId: Long) {
        viewModelScope.launch {
            _uiState.value = TransactionDetailUiState(isLoading = true)
            try {
                val response = jobDetailApiService.getJobDetail(jobId)
                if (response.isSuccessful && response.body()?.data != null) {
                    _uiState.value = TransactionDetailUiState(data = response.body()?.data)
                } else {
                    _uiState.value = TransactionDetailUiState(error = "Không thể tải dữ liệu chi tiết giao dịch")
                }
            } catch (e: Exception) {
                _uiState.value = TransactionDetailUiState(error = e.localizedMessage ?: "Có lỗi xảy ra")
            }
        }
    }
}
