package com.example.nexodigital.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.nexodigital.repository.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Controlador de Autenticación (AuthController)
 * Administra el estado y la lógica relacionada con la autenticación del usuario
 * (inicio de sesión, cierre de sesión, manejo de credenciales y control de errores)
 * sirviendo como puente entre la interfaz de usuario y el repositorio de autenticación.
 */
class AuthController(
    private val repository: AuthRepository
) {
    // Variables de estado observables vinculadas a la interfaz de usuario de Jetpack Compose

    // Almacena el texto ingresado en el campo de usuario
    var username by mutableStateOf("")

    // Almacena el texto ingresado en el campo de contraseña
    var password by mutableStateOf("")

    // Indicador booleano para saber si hay una petición de red en proceso (control de carga)
    var isLoading by mutableStateOf(false)

    // Almacena mensajes de error en caso de que falle la autenticación (nulo si no hay errores)
    var errorMessage by mutableStateOf<String?>(null)

    /**
     * Procesa la lógica de inicio de sesión.
     */
    fun performLogin(onSuccess: () -> Unit) {
        // Activa el indicador de carga y limpia cualquier error previo
        isLoading = true
        errorMessage = null

        // Ejecuta la petición de red en un hilo secundario para no bloquear la interfaz
        CoroutineScope(context = Dispatchers.IO).launch {
            val result = repository.login(username, password)

            // Regresa al hilo principal para actualizar los estados de la UI
            withContext(Dispatchers.Main) {
                isLoading = false
                if (result.isSuccess) {
                    onSuccess() // Credenciales correctas: ejecuta la acción de éxito
                } else {
                    // Si ocurre un fallo, extrae el mensaje de error o asigna uno por defecto
                    errorMessage = result.exceptionOrNull()?.message ?: "Credenciales inválidas"
                }
            }
        }
    }

    /**
     * Procesa la lógica de cierre de sesión.
     */
    fun performLogout(onLogout: () -> Unit) {
        repository.logout() // Limpia los datos de sesión almacenados localmente
        username = ""       // Restablece el campo de usuario
        password = ""       // Restablece el campo de contraseña
        errorMessage = null // Limpia los mensajes de error
        onLogout()          // Ejecuta la acción posterior al cierre de sesión
    }

    /**
     * Devuelve el rol actual del usuario autenticado obtenido desde el repositorio.
     */
    fun getRole(): String {
        return repository.getUserRole()
    }
}