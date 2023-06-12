package exercise.okebet.app.utils

import android.content.Context

class ThemeSharedPrefs(context: Context) {

    private val sharedPrefs = context.getSharedPreferences(THEME_PREFS, Context.MODE_PRIVATE)

    var isDarkTheme: Boolean
        get() = sharedPrefs.getBoolean(IS_DARK_THEME, false)
        set(value) = sharedPrefs.edit().putBoolean(IS_DARK_THEME, value).apply()

    private companion object {
        private const val IS_DARK_THEME = "IS_DARK_THEME"
        private const val THEME_PREFS = "THEME_PREFS"
    }
}