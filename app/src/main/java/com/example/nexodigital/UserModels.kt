package com.example.nexodigital

// Objeto para la petición de Login
data class LoginRequest(
    val username: String,
    val password: String
)

// Objeto para la respuesta de Login
data class LoginResponse(
    val token: String
)

// Objeto para representar al Usuario de la API
data class UserData(
    val id: Int,
    val username: String
)