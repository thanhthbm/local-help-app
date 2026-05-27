package com.localhelp.app.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localhelp.app.data.repository.FinanceRepository
import com.localhelp.app.model.response.CategoryDetailResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoryDetailUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val data: CategoryDetailResponse? = null
)

/**
 * ViewModel cho màn xem thống kê chi tiết theo danh mục.
 */
@HiltViewModel
class CategoryDetailViewModel @Inject constructor(
    private val financeRepository: FinanceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryDetailUiState())
    val uiState: StateFlow<CategoryDetailUiState> = _uiState.asStateFlow()

    /**
     * Tải chi tiết danh mục theo loại thống kê.
     *
     * isEarning = true -> type "earning"; false -> type "spending".
     */
    fun fetchDetails(categoryId: Long, isEarning: Boolean, month: Int, year: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val type = if (isEarning) "earning" else "spending"
            val result = financeRepository.getCategoryDetails(categoryId, type, month, year)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    data = result.getOrThrow()
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message
                )
            }
        }
    }
}
