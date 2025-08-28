package com.example.echoposts.domain.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

sealed class SettingsItem {
    data class Header(@StringRes val title: Int) : SettingsItem()

    data class SwitchItem(
        @StringRes val title: Int,
        @StringRes val subtitle: Int? = null,
        @DrawableRes val icon: Int,
        val isChecked: Boolean,
        val key: String
    ) : SettingsItem()

    data class ActionItem(
        @StringRes val title: Int,
        @StringRes val subtitle: Int? = null,
        @DrawableRes val icon: Int,
        val key: String,
        val showArrow: Boolean = true
    ) : SettingsItem()

    data class InfoItem(
        @StringRes val title: Int,
        val value: String,
        @DrawableRes val icon: Int
    ) : SettingsItem()

    data class DangerItem(
        @StringRes val title: Int,
        @StringRes val subtitle: Int? = null,
        @DrawableRes val icon: Int,
        val key: String
    ) : SettingsItem()
}