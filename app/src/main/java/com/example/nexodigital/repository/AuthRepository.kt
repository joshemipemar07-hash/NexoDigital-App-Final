package com.example.nexodigital.repository

import com.example.nexodigital.model.FakeStoreApiService
import com.example.nexodigital.model.LoginRequest
import com.example.nexodigital.model.SessionManager

class AuthRepository(
    private val apiService: FakeStoreApiService,
    private val sessionManager: SessionManager
) {
    suspend fun login(username: String, password: String): Result<Boolean> {
        return try {
            val response = apiService.login(LoginRequest(username, password))
            if (response.isSuccessful && response.body() != null) {
                val token = response.body()!!.token
                sessionManager.saveAuthToken(token)
                sessionManager.saveUserRole("Cliente")
                Result.success(true)
            } else {
                Result.failure(Exception("Credenciales inválidas"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.localizedMessage}"))
        }
    }

    fun logout() {
        sessionManager.clearSession()
    }

    fun getUserRole(): String {
        return sessionManager.getUserRole() ?: "Cliente"
    }

    fun isSessionActive(): Boolean {
        return sessionManager.getAuthToken() != null
    }
}