package com.example.echoposts.data.repository

import com.example.echoposts.data.local.UserSession
import com.example.echoposts.data.local.dao.UserDao
import com.example.echoposts.data.local.entity.User
import com.example.echoposts.domain.model.AuthState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AuthRepository @Inject constructor(
    private val userDao: UserDao,
    private val userSession: UserSession
) {

    suspend fun register(email: String, password: String): AuthState {
        return withContext(Dispatchers.IO) {
            try {
                val existingUser = userDao.getUserByEmail(email)
                if (existingUser != null) {
                    return@withContext AuthState.Error("User already exists")
                }

                val user = User(email = email, password = password)
                userDao.insertUser(user)
                AuthState.Success
            } catch (e: Exception) {
                AuthState.Error("Registration failed: ${e.message}")
            }
        }
    }

    suspend fun login(email: String, password: String): AuthState {
        return withContext(Dispatchers.IO) {
            try {
                val user = userDao.authenticateUser(email, password)
                if (user != null) {
                    userSession.setLoggedIn(true, email)
                    AuthState.Success
                } else {
                    AuthState.Error("Invalid credentials")
                }
            } catch (e: Exception) {
                AuthState.Error("Login failed: ${e.message}")
            }
        }
    }

    fun isLoggedIn(): Boolean = userSession.isLoggedIn()

    fun getCurrentUserEmail(): String? = userSession.getUserEmail()

    fun logout() = userSession.logout()
}