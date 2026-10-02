package com.example.nexodigital.model

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * Gestor de Sesión (SessionManager)
 * Administra el almacenamiento local para guardar, consultar y eliminar de manera segura información crítica del usuario activo,
 * tal como el token de autenticación y su rol dentro de la aplicación.
 */
class SessionManager(context: Context) {

    // Seconfigura en modo privado (solo accesible por la app)
    private val prefs: SharedPreferences = context.getSharedPreferences("NexoDigitalPrefs", Context.MODE_PRIVATE)

    /**
     * Almacena de forma persistente el token de autenticación del usuario.
     */
    fun saveAuthToken(token: String) {
        prefs.edit { putString("AUTH_TOKEN", token) }
    }

    //Recupera el token de autenticación almacenado localmente.
    fun getAuthToken(): String? {
        return prefs.getString("AUTH_TOKEN", null)
    }

    // Almacena de forma persistente el rol asignado al usuario autenticado.
    fun saveUserRole(role: String) {
        prefs.edit { putString("USER_ROLE", role) }
    }


    //Recupera el rol del usuario almacenado localmente.
    fun getUserRole(): String? {
        return prefs.getString("USER_ROLE", "Cliente")
    }

    /**
     * Borra todos los datos de sesión almacenados (cierre de sesión / logout).
     */
    fun clearSession() {
        prefs.edit { clear() }
    }
}