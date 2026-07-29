package com.localhelp.app.model.response

/**
 * DTO tổng quan thống kê thu/chi trả về từ /api/finance/overview.
 */
data class FinanceOverviewResponse(
    val totalAmount: Double,
    val percentageChange: Double,
    val trend: String,
    val weeklyChart: List<Double>,
    val categories: List<CategoryItemDTO>,
    val recentTransactions: List<TransactionItemDTO>
)

/**
 * Một dòng thống kê theo danh mục trong màn tổng quan.
 */
data class CategoryItemDTO(
    val id: Long,
    val name: String,
    val iconUrl: String?,
    val colorCode: String?,
    val amount: Double,
    val percentage: Double
)

/**
 * Một giao dịch hiển thị trong lịch sử thống kê.
 *
 * id chính là jobId, được dùng để điều hướng sang màn TransactionDetailScreen.
 */
data class TransactionItemDTO(
    val id: Long,
    val name: String,
    val serviceName: String,
    val amount: Double,
    val status: String,
    val dateStr: String,
    val dateTime: String,
    val iconUrl: String?,
    val colorCode: String?
)

/**
 * DTO chi tiết danh mục trả về từ /api/finance/categories/{categoryId}/details.
 */
data class CategoryDetailResponse(
    val categoryName: String,
    val totalAmount: Double,
    val subCategories: List<SubCategoryDTO>,
    val transactions: List<TransactionItemDTO>,
    val aiInsight: String
)

/**
 * Nhóm phụ trong màn chi tiết danh mục; backend hiện gom theo title của job.
 */
data class SubCategoryDTO(
    val subName: String,
    val amount: Double,
    val percentage: Double,
    val colorCode: String?
)
