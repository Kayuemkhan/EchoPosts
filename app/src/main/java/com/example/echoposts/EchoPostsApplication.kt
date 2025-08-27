package com.example.echoposts



import android.app.Application
import com.example.echoposts.data.ThemeManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class EchoPostsApplication : Application() {

    @Inject
    lateinit var themeManager: ThemeManager

    override fun onCreate() {
        super.onCreate()

        // Apply saved theme preference
        themeManager.applyTheme()
    }
}