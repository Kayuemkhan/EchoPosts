package com.example.echoposts.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.echoposts.data.ThemeManager
import com.example.echoposts.data.repository.AuthRepository
import com.example.echoposts.data.repository.SettingsRepository
import com.example.echoposts.domain.model.SettingsItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository,
    private val themeManager: ThemeManager
) : ViewModel() {

    private val _settingsItems = MutableStateFlow<List<SettingsItem>>(emptyList())
    val settingsItems: StateFlow<List<SettingsItem>> = _settingsItems.asStateFlow()

    private val _isDarkMode = MutableStateFlow(themeManager.isDarkModeEnabled())
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    init {
        loadSettings()
        observeThemeChanges()
    }

    private fun loadSettings() {
        _settingsItems.value = settingsRepository.getSettingsItems()
    }

    private fun observeThemeChanges() {
        viewModelScope.launch {
            themeManager.isDarkMode.collect { isDark ->
                _isDarkMode.value = isDark
                loadSettings() // Refresh to update switch state
            }
        }
    }

    fun onSwitchChanged(key: String, isChecked: Boolean) {
        when (key) {
            "dark_mode" -> {
                themeManager.setDarkMode(isChecked)
            }
        }
    }

    fun onItemClick(key: String) {
        // Handle item clicks in the fragment
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun getLibraries() = settingsRepository.getLibrariesInfo()
}