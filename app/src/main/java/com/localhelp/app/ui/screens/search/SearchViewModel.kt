package com.localhelp.app.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.localhelp.app.data.repository.JobRepository
import com.localhelp.app.data.repository.SearchRepository
import com.localhelp.app.model.response.JobResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.listOf

data class SearchUiState(
    val searchQuery: String = "",
    val recentSearches: List<String> = emptyList()
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: SearchRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch{
            repository.searchHistory.collect{ searchHistory ->
                _uiState.update { it.copy(recentSearches = searchHistory) }
            }
        }
    }

    fun updateQuery(query : String){
        _uiState.update {it.copy(searchQuery = query)}
    }

    fun addToRecentSearch(){
        val query = _uiState.value.searchQuery.trim()
        if(query.isBlank()) return

        viewModelScope.launch {
            repository.addSearchHistory(query)
        }
    }

    fun removeRecentSearch(query : String){
        viewModelScope.launch{
            repository.removeSearchHistory(query)
        }
    }

    fun clearAllRecentSearches(){
        viewModelScope.launch{
            repository.clearAllSearchHistory()
        }
    }
}