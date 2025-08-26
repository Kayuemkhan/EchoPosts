package com.example.echoposts.domain.model

data class PaginatedResponse<T>(
    val data: List<T>,
    val hasMore: Boolean,
    val totalCount: Int? = null
)