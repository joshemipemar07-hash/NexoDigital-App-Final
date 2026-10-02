package com.example.nexodigital.repository

import com.example.nexodigital.model.FakeStoreApiService
import com.example.nexodigital.model.LoginRequest
import com.example.nexodigital.model.SessionManager

/**
 * Repositorio de Autenticación (AuthRepository)
 * Actúa como intermediario (fuente única de la verdad) entre los controladores y las fuentes de datos
 * de autenticación, manejando las solicitudes de inicio de sesión vía red con Retrofit y delegando
 * el almacenamiento local de tokens y roles al gestor de sesión (SessionManager).
 */
class AuthRepository(
    private val apiService: FakeStoreApiService,
    private val sessionManager: SessionManager
) {
    /**
     * Realiza la petición de inicio de sesión enviando las credenciales a la API.
     * username Nombre de usuario o correo electrónico.
     * Contraseña de acceso.
     * Un objeto Result indicando éxito (true) o fallo con una excepción descriptiva.
     */
    suspend fun login(username: String, password: String): Result<Boolean> {
        return try {
            val response = apiService.login(LoginRequest(username, password))
            if (response.isSuccessful && response.body() != null) {
                val token = response.body()!!.token
                sessionManager.saveAuthToken(token)

                // --- CAMBIO CLAVE AQUÍ ---
                // Determinamos el rol según el usuario que ingresa
                val assignedRole = if (username.equals("mor_2314", ignoreCase = true)) {
                    "Administrador"
                } else {
                    "Cliente"
                }

                sessionManager.saveUserRole(assignedRole) // Guarda el rol correspondiente en las preferencias
                // --------------------------

                Result.success(true)
            } else {
                Result.failure(Exception("Credenciales inválidas"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.localizedMessage}"))
        }
    }
    /**
     * Cierra la sesión activa limpiando los datos almacenados localmente.
     */
    fun logout() {
        sessionManager.clearSession()
    }

    /**
     * Obtiene el rol actual del usuario autenticado almacenado en la sesión local.
     */
    fun getUserRole(): String {
        return sessionManager.getUserRole() ?: "Cliente"
    }

    /**
     * Verifica si existe una sesión activa comprobando la existencia de un token guardado.
     */
    fun isSessionActive(): Boolean {
        return sessionManager.getAuthToken() != null
    }
}