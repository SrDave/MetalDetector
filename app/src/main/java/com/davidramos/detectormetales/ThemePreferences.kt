package com.davidramos.detectormetales

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

object ThemePreferences {

    private const val PREFS_NAME = "theme_prefs"
    private const val KEY_THEME = "app_theme"

    fun saveTheme(context: Context, mode: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(KEY_THEME, mode).apply()
    }

    fun loadTheme(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_THEME, AppCompatDelegate.MODE_NIGHT_NO)
    }

    fun applyTheme(mode: Int) {
        AppCompatDelegate.setDefaultNightMode(mode)
    }
}
