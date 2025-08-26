package com.example.echoposts.domain.model

sealed class LoadingState {
    object Idle : LoadingState()
    object Loading : LoadingState()
    object LoadingMore : LoadingState()
    object Refreshing : LoadingState()
    data class Error(val message: String, val isRetryable: Boolean = true) : LoadingState()
    object Success : LoadingState()
}