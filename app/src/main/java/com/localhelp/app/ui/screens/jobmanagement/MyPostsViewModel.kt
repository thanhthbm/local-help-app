package com.localhelp.app.ui.screens.jobmanagement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localhelp.app.data.repository.JobRepository
import com.localhelp.app.model.response.JobResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state của danh sách công việc người dùng đã đăng.
 *
 * @property isLoading Đang tải trang đầu tiên.
 * @property isPaginating Đang tải thêm trang tiếp theo.
 * @property jobs Danh sách công việc đã đăng.
 * @property error Thông báo lỗi nếu tải danh sách thất bại.
 * @property isLastPage True khi không còn trang dữ liệu tiếp theo.
 */
data class MyPostsUiState(
    val isLoading: Boolean = false,
    val isPaginating: Boolean = false,
    val jobs: List<JobResponse> = emptyList(),
    val error: String? = null,
    val isLastPage: Boolean = false
)
/**
 * ViewModel quản lý UI state cho tab 'Việc đã đăng' trong JobManagementScreen.
 *
 * Implements infinite scroll (cuộn vô tận) với state machine gồm 3 biến:
 *   isLoading   – true khi load trang đầu tiên (hiện skeleton/spinner toàn màn hình).
 *   isPaginating – true khi load trang tiếp theo (hiện spinner cuối danh sách).
 *   isLastPage  – true khi không còn dữ liệu để load thêm.
 *
 * Guard tránh race condition: kiểm tra isLoading || isPaginating trước mỗi lần fetch.
 *
 */
@HiltViewModel
class MyPostsViewModel @Inject constructor(private val repository: JobRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(MyPostsUiState())
    val uiState: StateFlow<MyPostsUiState> = _uiState.asStateFlow()

    private var currentPage = 1

    init {
        loadMyPosts(isLoadMore = false)
    }
    /**
     * Tải danh sách công việc đã đăng, hỗ trợ phân trang vô tận.
     *
     * @param isLoadMore  false = load trang đầu (reset list),
     *                    true  = append thêm vào danh sách hiện tại.
     *
     * Logic phân trang:
     *   isLoadMore=false → reset currentPage = 1, clear jobs list.
     *   isLoadMore=true  → tăng currentPage, append jobs mới vào state.jobs.
     *   isLastPage=true  → return sớm, không gọi thêm API.
     *
     * onSuccess: so sánh currentPage >= meta.pages để biết có trang tiếp không.
     * onFailure: cập nhật error message vào state để View hiển thị.
     */
    fun loadMyPosts(isLoadMore: Boolean) {
        val currentState = _uiState.value
        if (currentState.isLoading || currentState.isPaginating) return
        if (isLoadMore && currentState.isLastPage) return

        if (isLoadMore) {
            _uiState.update { it.copy(isPaginating = true) }
        } else {
            currentPage = 1
            _uiState.update { it.copy(isLoading = true, error = null) }
        }

        viewModelScope.launch {
            val response = repository.getMyPosts(page = currentPage, size = 10)

            response.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isPaginating = false,
                        error = exception.message ?: "Không thể tải dữ liệu."
                    )
                }
            }.onSuccess { paginationData ->
                val newJobs = paginationData.result ?: emptyList()
                val meta = paginationData.meta

                val isLast = meta == null || currentPage >= meta.pages

                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        isPaginating = false,
                        jobs = if (isLoadMore) state.jobs + newJobs else newJobs,
                        isLastPage = isLast
                    )
                }

                if (!isLast) currentPage++
            }
        }
    }
}
