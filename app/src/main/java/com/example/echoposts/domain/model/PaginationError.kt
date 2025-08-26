package com.example.echoposts.domain.model

sealed class PaginationError : Exception() {
    object NetworkError : PaginationError()
    object NoMoreData : PaginationError()
    data class ApiError(val code: Int, override val message: String) : PaginationError()
    data class UnknownError(override val message: String) : PaginationError()
}
