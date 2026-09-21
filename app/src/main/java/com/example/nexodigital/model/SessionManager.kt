package com.example.nexodigital.model

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("NexoDigitalPrefs", Context.MODE_PRIVATE)

    fun saveAuthToken(token: String) {
        prefs.edit { putString("AUTH_TOKEN", token) }
    }

    fun getAuthToken(): String? {
        return prefs.getString("AUTH_TOKEN", null)
    }

    fun saveUserRole(role: String) {
        prefs.edit { putString("USER_ROLE", role) }
    }

    fun getUserRole(): String? {
        return prefs.getString("USER_ROLE", "Cliente")
    }

    fun clearSession() {
        prefs.edit { clear() }
    }
}