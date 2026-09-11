package com.example.nexodigital

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("nexo_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TOKEN = "user_token"
        private const val KEY_USER_ID = "user_id"
    }

    // Guardar sesión tras login exitoso (US01)
    fun saveSession(token: String, userId: Int) {
        prefs.edit {
            putString(KEY_TOKEN, token)
            putInt(KEY_USER_ID, userId)
        }
    }

    // Verificar si hay sesión activa
    fun isLoggedIn(): Boolean {
        return prefs.getString(KEY_TOKEN, null) != null
    }

    // Determina el rol según la regla de negocio (US01)
    fun getRole(): String? {
        if (!isLoggedIn()) return null
        val userId = prefs.getInt(KEY_USER_ID, -1)
        return when (userId) {
            1, 2 -> "Administrador"
            3 -> "Auditor"
            else -> "Cliente"
        }
    }

    // Limpieza de almacenamiento persistente (US02)
    fun clearSession() {
        prefs.edit {
            clear()
        }
    }
}