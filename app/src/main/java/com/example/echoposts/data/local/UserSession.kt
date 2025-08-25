package com.example.echoposts.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class UserSession @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sharedPrefs: SharedPreferences = 
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    
    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_EMAIL = "user_email"
    }
    
    fun setLoggedIn(isLoggedIn: Boolean, email: String? = null) {
        sharedPrefs.edit {
            putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
                .putString(KEY_USER_EMAIL, email)
        }
    }
    
    fun isLoggedIn(): Boolean = sharedPrefs.getBoolean(KEY_IS_LOGGED_IN, false)
    
    fun getUserEmail(): String? = sharedPrefs.getString(KEY_USER_EMAIL, null)
    
    fun logout() {
        sharedPrefs.edit {
            clear()
        }
    }
}
