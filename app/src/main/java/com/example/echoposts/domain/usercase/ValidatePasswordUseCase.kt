package com.example.echoposts.domain.usercase

import com.example.echoposts.domain.model.ValidationResult
import javax.inject.Inject

class ValidatePasswordUseCase @Inject constructor() {

    operator fun invoke(password: String): ValidationResult {
        if (password.length < 6) {
            return ValidationResult(false, "Password must be at least 6 characters")
        }

        val containsLettersAndDigits = password.any { it.isDigit() } &&
                password.any { it.isLetter() }
        if (!containsLettersAndDigits) {
            return ValidationResult(false, "Password must contain both letters and numbers")
        }

        return ValidationResult(true)
    }
}