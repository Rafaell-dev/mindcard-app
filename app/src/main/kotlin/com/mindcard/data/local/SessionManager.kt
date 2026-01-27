package com.mindcard.data.local

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.mindcard.data.model.User

class SessionManager(context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences("mindcard_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_USER_DATA = "user_data"
    }

    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(KEY_ACCESS_TOKEN, token)
        editor.apply()
    }

    fun fetchAuthToken(): String? {
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }

    fun saveUser(user: User) {
        val editor = prefs.edit()
        val json = gson.toJson(user)
        editor.putString(KEY_USER_DATA, json)
        editor.apply()
    }

    fun fetchUser(): User? {
        val json = prefs.getString(KEY_USER_DATA, null)
        return if (json != null) {
            gson.fromJson(json, User::class.java)
        } else {
            null
        }
    }

    fun clearAuthToken() {
        val editor = prefs.edit()
        editor.remove(KEY_ACCESS_TOKEN)
        editor.remove(KEY_USER_DATA)
        editor.apply()
    }
}
