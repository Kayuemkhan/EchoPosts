package com.example.echoposts.ui.auth

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.echoposts.data.repository.AuthRepository
import com.example.echoposts.domain.model.AuthState
import com.example.echoposts.domain.model.ValidationResult
import com.example.echoposts.domain.usercase.ValidateEmailUseCase
import com.example.echoposts.domain.usercase.ValidatePasswordUseCase
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
class LoginViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var authRepository: AuthRepository

    @Mock
    private lateinit var validateEmailUseCase: ValidateEmailUseCase

    @Mock
    private lateinit var validatePasswordUseCase: ValidatePasswordUseCase

    private lateinit var viewModel: LoginViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        viewModel = LoginViewModel(
            authRepository = authRepository,
            validateEmail = validateEmailUseCase,
            validatePassword = validatePasswordUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login with valid credentials should succeed`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "ValidPassword123!"
        val expectedSuccessState = AuthState.Success

        whenever(validateEmailUseCase.invoke(email)).thenReturn(
            ValidationResult(isValid = true)
        )
        whenever(validatePasswordUseCase.invoke(password)).thenReturn(
            ValidationResult(isValid = true)
        )
        whenever(authRepository.login(email, password)).thenReturn(expectedSuccessState)

        // Act & Assert
        viewModel.authState.test {
            // Initial state should be Idle
            assert(awaitItem() == AuthState.Idle)

            // Trigger login
            viewModel.login(email, password)

            // Should transition to Loading then Success
            assert(awaitItem() == AuthState.Loading)
            assert(awaitItem() == expectedSuccessState)
        }

        // Verify repository was called
        verify(authRepository).login(email, password)
    }

    @Test
    fun `login with invalid email should not call repository`() = runTest {
        // Arrange
        val email = "invalid-email"
        val password = "ValidPassword123!"
        val emailError = "Invalid email format"

        whenever(validateEmailUseCase.invoke(email)).thenReturn(
            ValidationResult(isValid = false, errorMessage = emailError)
        )
        whenever(validatePasswordUseCase.invoke(password)).thenReturn(
            ValidationResult(isValid = true)
        )

        // Act
        viewModel.login(email, password)

        // Assert
        viewModel.emailError.test {
            assert(awaitItem() == emailError)
        }

        // Repository should not be called
        verify(authRepository, never()).login(any(), any())
    }

    @Test
    fun `login with invalid password should not call repository`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "weak"
        val passwordError = "Password too weak"

        whenever(validateEmailUseCase.invoke(email)).thenReturn(
            ValidationResult(isValid = true)
        )
        whenever(validatePasswordUseCase.invoke(password)).thenReturn(
            ValidationResult(isValid = false, errorMessage = passwordError)
        )

        // Act
        viewModel.login(email, password)

        // Assert
        viewModel.passwordError.test {
            assert(awaitItem() == passwordError)
        }

        // Repository should not be called
        verify(authRepository, never()).login(any(), any())
    }

    @Test
    fun `login with both invalid email and password should show both errors`() = runTest {
        // Arrange
        val email = "invalid-email"
        val password = "weak"
        val emailError = "Invalid email format"
        val passwordError = "Password too weak"

        whenever(validateEmailUseCase.invoke(email)).thenReturn(
            ValidationResult(isValid = false, errorMessage = emailError)
        )
        whenever(validatePasswordUseCase.invoke(password)).thenReturn(
            ValidationResult(isValid = false, errorMessage = passwordError)
        )

        // Act
        viewModel.login(email, password)

        // Assert
        viewModel.emailError.test {
            assert(awaitItem() == emailError)
        }

        viewModel.passwordError.test {
            assert(awaitItem() == passwordError)
        }

        // Repository should not be called
        verify(authRepository, never()).login(any(), any())
    }

    @Test
    fun `login failure should return error state`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "ValidPassword123!"
        val errorMessage = "Authentication failed"
        val expectedErrorState = AuthState.Error(errorMessage)

        whenever(validateEmailUseCase.invoke(email)).thenReturn(
            ValidationResult(isValid = true)
        )
        whenever(validatePasswordUseCase.invoke(password)).thenReturn(
            ValidationResult(isValid = true)
        )
        whenever(authRepository.login(email, password)).thenReturn(expectedErrorState)

        // Act & Assert
        viewModel.authState.test {
            // Initial state should be Idle
            assert(awaitItem() == AuthState.Idle)

            // Trigger login
            viewModel.login(email, password)

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
        val emailError = "Invalid email format"
        val passwordError = "Password too weak"

        whenever(validateEmailUseCase.invoke(email)).thenReturn(
            ValidationResult(isValid = false, errorMessage = emailError)
        )
        whenever(validatePasswordUseCase.invoke(password)).thenReturn(
            ValidationResult(isValid = false, errorMessage = passwordError)
        )

        viewModel.login(email, password)

        // Act
        viewModel.clearErrors()

        // Assert
        viewModel.emailError.test {
            assert(awaitItem() == null)
        }

        viewModel.passwordError.test {
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
    }

    @Test
    fun `empty email and password should trigger validation`() = runTest {
        // Arrange
        val email = ""
        val password = ""
        val emailError = "Email cannot be empty"
        val passwordError = "Password cannot be empty"

        whenever(validateEmailUseCase.invoke(email)).thenReturn(
            ValidationResult(isValid = false, errorMessage = emailError)
        )
        whenever(validatePasswordUseCase.invoke(password)).thenReturn(
            ValidationResult(isValid = false, errorMessage = passwordError)
        )

        // Act
        viewModel.login(email, password)

        // Assert
        viewModel.emailError.test {
            assert(awaitItem() == emailError)
        }

        viewModel.passwordError.test {
            assert(awaitItem() == passwordError)
        }

        // Auth state should remain Idle since validation failed
        viewModel.authState.test {
            assert(awaitItem() == AuthState.Idle)
        }
    }
}