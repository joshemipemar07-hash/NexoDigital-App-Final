package com.example.nexodigital

class AuthRepository(
    private val apiService: FakeStoreApiService,
    private val sessionManager: SessionManager
) {
    // Encapsula las llamadas HTTP y la regla de asignacion de roles
    suspend fun authenticate(username: String, password: String): Result<Boolean> {
        return try {
            val loginResponse = apiService.login(LoginRequest(username, password))

            if (loginResponse.isSuccessful && loginResponse.body() != null) {
                val token = loginResponse.body()!!.token

                // Obtener ID del usuario dinamicamente desde el servidor
                val usersResponse = apiService.getUsers()
                var realUserId = 4 // Por defecto Cliente

                if (usersResponse.isSuccessful && usersResponse.body() != null) {
                    val userObj = usersResponse.body()!!.find { it.username == username }
                    if (userObj != null) {
                        realUserId = userObj.id
                    }
                }

                // Guardar la sesion mediante la clase SessionManager
                sessionManager.saveSession(token, realUserId)
                Result.success(true)
            } else {
                Result.failure(Exception("Usuario o contraseña inválidos"))
            }
        } catch (_: Exception) {
            Result.failure(Exception("Error de conexión con la API"))
        }
    }

    fun logout() {
        sessionManager.clearSession()
    }

    fun isSessionActive(): Boolean = sessionManager.isLoggedIn()
    fun getUserRole(): String = sessionManager.getRole() ?: "Cliente"
}