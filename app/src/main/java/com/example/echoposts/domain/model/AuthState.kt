package com.example.echoposts.domain.model

sealed class AuthState {
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
    object Idle : AuthState()
}