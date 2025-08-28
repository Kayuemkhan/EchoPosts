package com.example.echoposts


import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import androidx.appcompat.app.AppCompatDelegate

@HiltAndroidApp
class EchoPostsApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        applySavedTheme()
    }

    private fun applySavedTheme() {
        val prefs = getSharedPreferences("theme_prefs", MODE_PRIVATE)
        val themeMode = prefs.getInt("theme_mode", 2) // Default to system (2)

        val mode = when (themeMode) {
            0 -> AppCompatDelegate.MODE_NIGHT_NO      // Light
            1 -> AppCompatDelegate.MODE_NIGHT_YES     // Dark
            2 -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM // System
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }

        AppCompatDelegate.setDefaultNightMode(mode)
    }
}
