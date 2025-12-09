package com.example.footballchik.utils

import android.content.Context
import android.content.SharedPreferences

class AuthManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "footballchik_auth",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_EMAIL = "email"
        private const val KEY_ROLE = "role"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    // Логин (сохраняем пользователя)
    fun login(userId: String, email: String, role: String = "viewer") {
        prefs.edit().apply {
            putString(KEY_USER_ID, userId)
            putString(KEY_EMAIL, email)
            putString(KEY_ROLE, role)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    // Разлогин
    fun logout() {
        prefs.edit().apply {
            clear()
            apply()
        }
    }

    // Получить ID пользователя
    fun getUserId(): String {
        return prefs.getString(KEY_USER_ID, "") ?: ""
    }

    // Получить email пользователя
    fun getEmail(): String {
        return prefs.getString(KEY_EMAIL, "") ?: ""
    }

    // Получить роль пользователя
    fun getRole(): String {
        return prefs.getString(KEY_ROLE, "viewer") ?: "viewer"
    }

    // Проверить залогинен ли
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }
}
