package exercise.okebet.app.utils

import android.content.Context

class SharedPrefs(context: Context) {

    private val sharedPrefs = context.getSharedPreferences(URL_PREFS, Context.MODE_PRIVATE)

    var url: String?
        get() = sharedPrefs.getString(URL, null)
        set(value) = sharedPrefs.edit().putString(URL, value).apply()

    private companion object {
        private const val URL = "URL"
        private const val URL_PREFS = "URL_PREFS"
    }
}