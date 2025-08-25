package com.example.echoposts.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.echoposts.data.repository.AuthRepository
import com.example.echoposts.domain.model.AuthState
import com.example.echoposts.domain.usercase.ValidateEmailUseCase
import com.example.echoposts.domain.usercase.ValidatePasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val validateEmail: ValidateEmailUseCase,
    private val validatePassword: ValidatePasswordUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError.asStateFlow()

    fun login(email: String, password: String) {
        val emailResult = validateEmail(email)
        val passwordResult = validatePassword(password)

        _emailError.value = emailResult.errorMessage
        _passwordError.value = passwordResult.errorMessage

        if (!emailResult.isValid || !passwordResult.isValid) {
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.login(email, password)
            _authState.value = result
        }
    }

    fun clearErrors() {
        _emailError.value = null
        _passwordError.value = null
        _authState.value = AuthState.Idle
    }
}