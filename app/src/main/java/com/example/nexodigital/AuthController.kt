package com.example.nexodigital

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthController(
    private val repository: AuthRepository
) {
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun performLogin(context: Context, onSuccess: () -> Unit) {
        errorMessage = null

        if (username.isBlank() || password.isBlank()) {
            errorMessage = "Por favor, ingresa usuario y contraseña"
            return
        }

        if (!isInternetAvailable(context)) {
            errorMessage = "Sin conexión a internet. Revisa tu red."
            return
        }

        isLoading = true
        CoroutineScope(Dispatchers.IO).launch {
            val result = repository.authenticate(username, password)
            withContext(Dispatchers.Main) {
                isLoading = false
                result.onSuccess {
                    onSuccess()
                }.onFailure { error ->
                    errorMessage = error.message
                }
            }
        }
    }

    fun performLogout(onLogoutDone: () -> Unit) {
        // Limpieza de datos persistentes (SharedPreferences)
        repository.logout()

        // Reinicio de estados temporales en memoria (Campos de texto)
        username = ""
        password = ""
        errorMessage = null

        onLogoutDone()
    }

    fun getRole(): String = repository.getUserRole()
}