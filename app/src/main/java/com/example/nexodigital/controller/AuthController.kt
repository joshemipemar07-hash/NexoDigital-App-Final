package com.example.nexodigital.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.nexodigital.repository.AuthRepository
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

    // Devuelve el rol
    fun getRole(): String {
        return repository.getUserRole()
    }

    // Lógica de inicio de sesión
    fun performLogin(onSuccess: () -> Unit) {
        isLoading = true
        errorMessage = null

        CoroutineScope(Dispatchers.IO).launch {
            val result = repository.login(username, password)
            withContext(Dispatchers.Main) {
                isLoading = false
                if (result.isSuccess) {
                    onSuccess()
                } else {
                    errorMessage = result.exceptionOrNull()?.message ?: "Credenciales inválidas"
                }
            }
        }
    }

    // Lógica de cierre de sesión
    fun performLogout(onLogout: () -> Unit) {
        repository.logout()
        username = ""
        password = ""
        errorMessage = null
        onLogout()
    }
}