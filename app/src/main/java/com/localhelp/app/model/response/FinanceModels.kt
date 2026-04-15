package com.localhelp.app.model.response

data class FinanceOverviewResponse(
    val totalAmount: Double,
    val percentageChange: Double,
    val trend: String,
    val weeklyChart: List<Double>,
    val categories: List<CategoryItemDTO>,
    val recentTransactions: List<TransactionItemDTO>
)

data class CategoryItemDTO(
    val id: Long,
    val name: String,
    val iconUrl: String?,
    val colorCode: String?,
    val amount: Double,
    val percentage: Double
)

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

data class CategoryDetailResponse(
    val categoryName: String,
    val totalAmount: Double,
    val subCategories: List<SubCategoryDTO>,
    val transactions: List<TransactionItemDTO>,
    val aiInsight: String
)

data class SubCategoryDTO(
    val subName: String,
    val amount: Double,
    val percentage: Double,
    val colorCode: String?
)
