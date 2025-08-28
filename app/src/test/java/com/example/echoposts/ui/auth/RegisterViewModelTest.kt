package com.example.echoposts.ui.auth

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.echoposts.data.repository.AuthRepository
import com.example.echoposts.domain.model.AuthState
import com.example.echoposts.domain.model.ValidationResult
import com.example.echoposts.domain.usercase.ValidateEmailUseCase
import com.example.echoposts.domain.usercase.ValidatePasswordUseCase
import com.example.echoposts.domain.usercase.ValidateRepeatedPasswordUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var authRepository: AuthRepository
    
    @Mock
    private lateinit var validateEmailUseCase: ValidateEmailUseCase
    
    @Mock
    private lateinit var validatePasswordUseCase: ValidatePasswordUseCase
    
    @Mock
    private lateinit var validateRepeatedPasswordUseCase: ValidateRepeatedPasswordUseCase

    private lateinit var viewModel: RegisterViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        
        viewModel = RegisterViewModel(
            authRepository = authRepository,
            validateEmail = validateEmailUseCase,
            validatePassword = validatePasswordUseCase,
            validateRepeatedPassword = validateRepeatedPasswordUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `register with valid data should succeed`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "ValidPassword123!"
        val confirmPassword = "ValidPassword123!"
        val expectedSuccessState = AuthState.Success

        whenever(validateEmailUseCase.invoke(email)).thenReturn(
            ValidationResult(isValid = true)
        )
        whenever(validatePasswordUseCase.invoke(password)).thenReturn(
            ValidationResult(isValid = true)
        )
        whenever(validateRepeatedPasswordUseCase.invoke(password, confirmPassword)).thenReturn(
            ValidationResult(isValid = true)
        )
        whenever(authRepository.register(email, password)).thenReturn(expectedSuccessState)

        // Act & Assert
        viewModel.authState.test {
            // Initial state should be Idle
            assert(awaitItem() == AuthState.Idle)

            // Trigger registration
            viewModel.register(email, password, confirmPassword)

            // Should transition to Loading then Success
            assert(awaitItem() == AuthState.Loading)
            assert(awaitItem() == expectedSuccessState)
        }

        // Verify repository was called
        verify(authRepository).register(email, password)
    }

    @Test
    fun `register with invalid email should not call repository`() = runTest {
        // Arrange
        val email = "invalid-email"
        val password = "ValidPassword123!"
        val confirmPassword = "ValidPassword123!"
        val emailError = "Invalid email format"

        whenever(validateEmailUseCase.invoke(email)).thenReturn(
            ValidationResult(isValid = false, errorMessage = emailError)
        )
        whenever(validatePasswordUseCase.invoke(password)).thenReturn(
            ValidationResult(isValid = true)
        )
        whenever(validateRepeatedPasswordUseCase.invoke(password, confirmPassword)).thenReturn(
            ValidationResult(isValid = true)
        )

        // Act
        viewModel.register(email, password, confirmPassword)

        // Assert
        viewModel.emailError.test {
            assert(awaitItem() == emailError)
        }

        // Repository should not be called
        verify(authRepository, never()).register(any(), any())
    }

    @Test
    fun `register with invalid password should not call repository`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "weak"
        val confirmPassword = "weak"
        val passwordError = "Password too weak"

        whenever(validateEmailUseCase.invoke(email)).thenReturn(
            ValidationResult(isValid = true)
        )
        whenever(validatePasswordUseCase.invoke(password)).thenReturn(
            ValidationResult(isValid = false, errorMessage = passwordError)
        )
        whenever(validateRepeatedPasswordUseCase.invoke(password, confirmPassword)).thenReturn(
            ValidationResult(isValid = true)
        )

        // Act
        viewModel.register(email, password, confirmPassword)

        // Assert
        viewModel.passwordError.test {
            assert(awaitItem() == passwordError)
        }

        // Repository should not be called
        verify(authRepository, never()).register(any(), any())
    }

    @Test
    fun `register with mismatched passwords should not call repository`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "ValidPassword123!"
        val confirmPassword = "DifferentPassword123!"
        val confirmPasswordError = "Passwords do not match"

        whenever(validateEmailUseCase.invoke(email)).thenReturn(
            ValidationResult(isValid = true)
        )
        whenever(validatePasswordUseCase.invoke(password)).thenReturn(
            ValidationResult(isValid = true)
        )
        whenever(validateRepeatedPasswordUseCase.invoke(password, confirmPassword)).thenReturn(
            ValidationResult(isValid = false, errorMessage = confirmPasswordError)
        )

        // Act
        viewModel.register(email, password, confirmPassword)

        // Assert
        viewModel.confirmPasswordError.test {
            assert(awaitItem() == confirmPasswordError)
        }

        // Repository should not be called
        verify(authRepository, never()).register(any(), any())
    }

    @Test
    fun `register with all invalid fields should show all errors`() = runTest {
        // Arrange
        val email = "invalid-email"
        val password = "weak"
        val confirmPassword = "different"
        val emailError = "Invalid email format"
        val passwordError = "Password too weak"
        val confirmPasswordError = "Passwords do not match"

        whenever(validateEmailUseCase.invoke(email)).thenReturn(
            ValidationResult(isValid = false, errorMessage = emailError)
        )
        whenever(validatePasswordUseCase.invoke(password)).thenReturn(
            ValidationResult(isValid = false, errorMessage = passwordError)
        )
        whenever(validateRepeatedPasswordUseCase.invoke(password, confirmPassword)).thenReturn(
            ValidationResult(isValid = false, errorMessage = confirmPasswordError)
        )

        // Act
        viewModel.register(email, password, confirmPassword)

        // Assert
        viewModel.emailError.test {
            assert(awaitItem() == emailError)
        }

        viewModel.passwordError.test {
            assert(awaitItem() == passwordError)
        }

        viewModel.confirmPasswordError.test {
            assert(awaitItem() == confirmPasswordError)
        }

        // Repository should not be called
        verify(authRepository, never()).register(any(), any())
    }

    @Test
    fun `register failure should return error state`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "ValidPassword123!"
        val confirmPassword = "ValidPassword123!"
        val errorMessage = "Registration failed - email already exists"
        val expectedErrorState = AuthState.Error(errorMessage)

        whenever(validateEmailUseCase.invoke(email)).thenReturn(
            ValidationResult(isValid = true)
        )
        whenever(validatePasswordUseCase.invoke(password)).thenReturn(
            ValidationResult(isValid = true)
        )
        whenever(validateRepeatedPasswordUseCase.invoke(password, confirmPassword)).thenReturn(
            ValidationResult(isValid = true)
        )
        whenever(authRepository.register(email, password)).thenReturn(expectedErrorState)

        // Act & Assert
        viewModel.authState.test {
            // Initial state should be Idle
            assert(awaitItem() == AuthState.Idle)

            // Trigger registration
            viewModel.register(email, password, confirmPassword)

            // Should transition to Loading then Error
            assert(awaitItem() == AuthState.Loading)
            assert(awaitItem() == expectedErrorState)
        }
    }

    @Test
    fun `clearErrors should reset all error states and auth state to idle`() = runTest {
        // Arrange - First set some errors
        val email = "invalid-email"
        val password = "weak"
        val confirmPassword = "different"
        val emailError = "Invalid email format"
        val passwordError = "Password too weak"
        val confirmPasswordError = "Passwords do not match"

        whenever(validateEmailUseCase.invoke(email)).thenReturn(
            ValidationResult(isValid = false, errorMessage = emailError)
        )
        whenever(validatePasswordUseCase.invoke(password)).thenReturn(
            ValidationResult(isValid = false, errorMessage = passwordError)
        )
        whenever(validateRepeatedPasswordUseCase.invoke(password, confirmPassword)).thenReturn(
            ValidationResult(isValid = false, errorMessage = confirmPasswordError)
        )

        viewModel.register(email, password, confirmPassword)

        // Act
        viewModel.clearErrors()

        // Assert
        viewModel.emailError.test {
            assert(awaitItem() == null)
        }

        viewModel.passwordError.test {
            assert(awaitItem() == null)
        }

        viewModel.confirmPasswordError.test {
            assert(awaitItem() == null)
        }

        viewModel.authState.test {
            assert(awaitItem() == AuthState.Idle)
        }
    }

    @Test
    fun `initial state should be idle with no errors`() = runTest {
        // Assert initial states
        viewModel.authState.test {
            assert(awaitItem() == AuthState.Idle)
        }

        viewModel.emailError.test {
            assert(awaitItem() == null)
        }

        viewModel.passwordError.test {
            assert(awaitItem() == null)
        }

        viewModel.confirmPasswordError.test {
            assert(awaitItem() == null)
        }
    }

    @Test
    fun `empty fields should trigger validation errors`() = runTest {
        // Arrange
        val email = ""
        val password = ""
        val confirmPassword = ""
        val emailError = "Email cannot be empty"
        val passwordError = "Password cannot be empty"
        val confirmPasswordError = "Please confirm your password"

        whenever(validateEmailUseCase.invoke(email)).thenReturn(
            ValidationResult(isValid = false, errorMessage = emailError)
        )
        whenever(validatePasswordUseCase.invoke(password)).thenReturn(
            ValidationResult(isValid = false, errorMessage = passwordError)
        )
        whenever(validateRepeatedPasswordUseCase.invoke(password, confirmPassword)).thenReturn(
            ValidationResult(isValid = false, errorMessage = confirmPasswordError)
        )

        // Act
        viewModel.register(email, password, confirmPassword)

        // Assert
        viewModel.emailError.test {
            assert(awaitItem() == emailError)
        }

        viewModel.passwordError.test {
            assert(awaitItem() == passwordError)
        }

        viewModel.confirmPasswordError.test {
            assert(awaitItem() == confirmPasswordError)
        }

        // Auth state should remain Idle since validation failed
        viewModel.authState.test {
            assert(awaitItem() == AuthState.Idle)
        }
    }

    @Test
    fun `register with valid email and password but invalid confirm password should not call repository`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "ValidPassword123!"
        val confirmPassword = ""
        val confirmPasswordError = "Please confirm your password"

        whenever(validateEmailUseCase.invoke(email)).thenReturn(
            ValidationResult(isValid = true)
        )
        whenever(validatePasswordUseCase.invoke(password)).thenReturn(
            ValidationResult(isValid = true)
        )
        whenever(validateRepeatedPasswordUseCase.invoke(password, confirmPassword)).thenReturn(
            ValidationResult(isValid = false, errorMessage = confirmPasswordError)
        )

        // Act
        viewModel.register(email, password, confirmPassword)

        // Assert
        viewModel.confirmPasswordError.test {
            assert(awaitItem() == confirmPasswordError)
        }

        // Other errors should be null
        viewModel.emailError.test {
            assert(awaitItem() == null)
        }

        viewModel.passwordError.test {
            assert(awaitItem() == null)
        }

        // Repository should not be called
        verify(authRepository, never()).register(any(), any())
    }
}