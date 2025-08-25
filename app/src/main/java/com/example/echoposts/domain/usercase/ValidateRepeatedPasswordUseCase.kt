package com.example.echoposts.domain.usercase

import com.example.echoposts.domain.model.ValidationResult
import javax.inject.Inject

class ValidateRepeatedPasswordUseCase @Inject constructor() {
    
    operator fun invoke(password: String, repeatedPassword: String): ValidationResult {
        if (password != repeatedPassword) {
            return ValidationResult(false, "Passwords don't match")
        }
        return ValidationResult(true)
    }
}