package com.example.echoposts.data

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = 
        context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
    
    private val _isDarkMode = MutableStateFlow(isDarkModeEnabled())
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()
    
    companion object {
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_FOLLOW_SYSTEM = "follow_system"
    }
    
    fun isDarkModeEnabled(): Boolean {
        return prefs.getBoolean(KEY_DARK_MODE, false)
    }
    
    fun isFollowSystemEnabled(): Boolean {
        return prefs.getBoolean(KEY_FOLLOW_SYSTEM, true)
    }
    
    fun setDarkMode(enabled: Boolean) {
        prefs.edit()
            .putBoolean(KEY_DARK_MODE, enabled)
            .putBoolean(KEY_FOLLOW_SYSTEM, false)
            .apply()
        
        _isDarkMode.value = enabled
        applyTheme()
    }
    
    fun setFollowSystem(enabled: Boolean) {
        prefs.edit()
            .putBoolean(KEY_FOLLOW_SYSTEM, enabled)
            .apply()
        
        if (enabled) {
            _isDarkMode.value = false
        }
        applyTheme()
    }
    
    fun applyTheme() {
        val mode = when {
            isFollowSystemEnabled() -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            isDarkModeEnabled() -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }
}