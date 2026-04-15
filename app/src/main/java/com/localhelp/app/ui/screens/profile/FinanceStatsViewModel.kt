package com.localhelp.app.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localhelp.app.data.repository.FinanceRepository
import com.localhelp.app.model.response.CategoryItemDTO
import com.localhelp.app.model.response.FinanceOverviewResponse
import com.localhelp.app.model.response.TransactionItemDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class FinanceOverviewUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val totalAmount: Double = 0.0,
    val percentageChange: Double = 0.0,
    val trend: String = "UP",
    val weeklyChart: List<Double> = emptyList(),
    val categories: List<CategoryItemDTO> = emptyList(),
    val recentTransactions: List<TransactionItemDTO> = emptyList()
)

@HiltViewModel
class FinanceStatsViewModel @Inject constructor(
    private val financeRepository: FinanceRepository
) : ViewModel() {

    private val _spendingState = MutableStateFlow(FinanceOverviewUiState())
    val spendingState: StateFlow<FinanceOverviewUiState> = _spendingState.asStateFlow()

    private val _earningState = MutableStateFlow(FinanceOverviewUiState())
    val earningState: StateFlow<FinanceOverviewUiState> = _earningState.asStateFlow()

    fun fetchData(month: Int, year: Int) {
        fetchSpending(month, year)
        fetchEarning(month, year)
    }

    private fun fetchSpending(month: Int, year: Int) {
        viewModelScope.launch {
            _spendingState.value = _spendingState.value.copy(isLoading = true, errorMessage = null)
            val result = financeRepository.getFinanceOverview("spending", month, year)
            if (result.isSuccess) {
                val data = result.getOrThrow()
                _spendingState.value = mapDataToState(data)
            } else {
                _spendingState.value = _spendingState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message
                )
            }
        }
    }

    private fun fetchEarning(month: Int, year: Int) {
        viewModelScope.launch {
            _earningState.value = _earningState.value.copy(isLoading = true, errorMessage = null)
            val result = financeRepository.getFinanceOverview("earning", month, year)
            if (result.isSuccess) {
                val data = result.getOrThrow()
                _earningState.value = mapDataToState(data)
            } else {
                _earningState.value = _earningState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message
                )
            }
        }
    }

    private fun mapDataToState(data: FinanceOverviewResponse): FinanceOverviewUiState {
        return FinanceOverviewUiState(
            isLoading = false,
            errorMessage = null,
            totalAmount = data.totalAmount,
            percentageChange = data.percentageChange,
            trend = data.trend,
            weeklyChart = data.weeklyChart,
            categories = data.categories,
            recentTransactions = data.recentTransactions
        )
    }
}
