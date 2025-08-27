package com.example.echoposts.data.repository

import android.content.Context
import android.content.pm.PackageManager
import com.example.echoposts.R
import com.example.echoposts.data.ThemeManager
import com.example.echoposts.domain.model.SettingsItem
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val themeManager: ThemeManager
) {
    
    fun getSettingsItems(): List<SettingsItem> {
        return buildList {
            // Appearance Section
            add(SettingsItem.Header(R.string.settings_appearance))
            add(
                SettingsItem.SwitchItem(
                    title = R.string.settings_dark_mode,
                    subtitle = R.string.settings_dark_mode_subtitle,
                    icon = R.drawable.ic_dark_mode,
                    isChecked = themeManager.isDarkModeEnabled(),
                    key = "dark_mode"
                )
            )
            
            // Account Section
            add(SettingsItem.Header(R.string.settings_account))
            add(
                SettingsItem.ActionItem(
                    title = R.string.settings_profile,
                    subtitle = R.string.settings_profile_subtitle,
                    icon = R.drawable.ic_person,
                    key = "profile"
                )
            )
            add(
                SettingsItem.ActionItem(
                    title = R.string.settings_privacy,
                    subtitle = R.string.settings_privacy_subtitle,
                    icon = R.drawable.ic_privacy,
                    key = "privacy"
                )
            )
            
            // Data & Storage Section
            add(SettingsItem.Header(R.string.settings_data_storage))
            add(
                SettingsItem.ActionItem(
                    title = R.string.settings_clear_cache,
                    subtitle = R.string.settings_clear_cache_subtitle,
                    icon = R.drawable.ic_storage,
                    key = "clear_cache"
                )
            )
            add(
                SettingsItem.ActionItem(
                    title = R.string.settings_data_usage,
                    subtitle = R.string.settings_data_usage_subtitle,
                    icon = R.drawable.ic_data_usage,
                    key = "data_usage"
                )
            )
            
            // About Section
            add(SettingsItem.Header(R.string.settings_about))
            add(
                SettingsItem.InfoItem(
                    title = R.string.settings_version,
                    value = getAppVersion(),
                    icon = R.drawable.ic_gavel
                )
            )
            add(
                SettingsItem.ActionItem(
                    title = R.string.settings_libraries,
                    subtitle = R.string.settings_libraries_subtitle,
                    icon = R.drawable.ic_code,
                    key = "libraries"
                )
            )
            add(
                SettingsItem.ActionItem(
                    title = R.string.settings_about_app,
                    subtitle = R.string.settings_about_app_subtitle,
                    icon = R.drawable.ic_info_outline,
                    key = "about"
                )
            )
            add(
                SettingsItem.ActionItem(
                    title = R.string.settings_licenses,
                    subtitle = R.string.settings_licenses_subtitle,
                    icon = R.drawable.ic_gavel,
                    key = "licenses"
                )
            )
            
            // Support Section
            add(SettingsItem.Header(R.string.settings_support))
            add(
                SettingsItem.ActionItem(
                    title = R.string.settings_help,
                    subtitle = R.string.settings_help_subtitle,
                    icon = R.drawable.ic_help,
                    key = "help"
                )
            )
            add(
                SettingsItem.ActionItem(
                    title = R.string.settings_feedback,
                    subtitle = R.string.settings_feedback_subtitle,
                    icon = R.drawable.ic_feedback,
                    key = "feedback"
                )
            )
            add(
                SettingsItem.ActionItem(
                    title = R.string.settings_rate_app,
                    subtitle = R.string.settings_rate_app_subtitle,
                    icon = R.drawable.ic_star,
                    key = "rate_app"
                )
            )
            
            // Danger Zone
            add(SettingsItem.Header(R.string.settings_danger_zone))
            add(
                SettingsItem.DangerItem(
                    title = R.string.settings_logout,
                    subtitle = R.string.settings_logout_subtitle,
                    icon = R.drawable.ic_logout,
                    key = "logout"
                )
            )
        }
    }
    
    private fun getAppVersion(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            "${packageInfo.versionName} (${packageInfo.longVersionCode})"
        } catch (e: PackageManager.NameNotFoundException) {
            "Unknown"
        }
    }
    
    fun getLibrariesInfo(): List<LibraryInfo> {
        return listOf(
            LibraryInfo(
                name = "Android Jetpack",
                description = "A suite of libraries to help developers build robust apps",
                version = "Various",
                license = "Apache License 2.0",
                url = "https://developer.android.com/jetpack"
            ),
            LibraryInfo(
                name = "Kotlin",
                description = "A modern programming language for Android development",
                version = "2.0.21",
                license = "Apache License 2.0",
                url = "https://kotlinlang.org/"
            ),
            LibraryInfo(
                name = "Room",
                description = "Persistence library providing abstraction layer over SQLite",
                version = "2.6.1",
                license = "Apache License 2.0",
                url = "https://developer.android.com/training/data-storage/room"
            ),
            LibraryInfo(
                name = "Retrofit",
                description = "A type-safe HTTP client for Android and Java",
                version = "2.11.0",
                license = "Apache License 2.0",
                url = "https://square.github.io/retrofit/"
            ),
            LibraryInfo(
                name = "Hilt",
                description = "Dependency injection library for Android",
                version = "2.51.1",
                license = "Apache License 2.0",
                url = "https://dagger.dev/hilt/"
            ),
            LibraryInfo(
                name = "Kotlin Coroutines",
                description = "Library for asynchronous programming",
                version = "1.8.1",
                license = "Apache License 2.0",
                url = "https://kotlinlang.org/docs/coroutines-overview.html"
            ),
            LibraryInfo(
                name = "Material Components",
                description = "Material Design components for Android",
                version = "1.12.0",
                license = "Apache License 2.0",
                url = "https://material.io/develop/android"
            ),
            LibraryInfo(
                name = "Navigation Component",
                description = "Framework for navigating between destinations",
                version = "2.9.3",
                license = "Apache License 2.0",
                url = "https://developer.android.com/guide/navigation"
            ),
            LibraryInfo(
                name = "OkHttp",
                description = "An HTTP client for Android and Java applications",
                version = "4.12.0",
                license = "Apache License 2.0",
                url = "https://square.github.io/okhttp/"
            ),
            LibraryInfo(
                name = "Gson",
                description = "A Java serialization/deserialization library",
                version = "2.11.0",
                license = "Apache License 2.0",
                url = "https://github.com/google/gson"
            )
        )
    }
}

data class LibraryInfo(
    val name: String,
    val description: String,
    val version: String,
    val license: String,
    val url: String
)