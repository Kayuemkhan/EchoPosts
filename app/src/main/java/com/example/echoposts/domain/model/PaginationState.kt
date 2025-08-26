package com.example.echoposts.domain.model

data class PaginationState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMoreData: Boolean = true,
    val currentPage: Int = 0,
    val error: String? = null
)