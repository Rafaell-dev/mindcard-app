package com.mindcard.data.local

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences("mindcard_prefs", Context.MODE_PRIVATE)

    companion object {
        const val KEY_ACCESS_TOKEN = "access_token"
    }

    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(KEY_ACCESS_TOKEN, token)
        editor.apply()
    }

    fun fetchAuthToken(): String? {
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }

    fun clearAuthToken() {
        val editor = prefs.edit()
        editor.remove(KEY_ACCESS_TOKEN)
        editor.apply()
    }
}
