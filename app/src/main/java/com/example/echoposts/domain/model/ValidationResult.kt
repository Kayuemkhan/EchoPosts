package com.example.echoposts.domain.model

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)