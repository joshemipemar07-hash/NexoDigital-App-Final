package com.example.nexodigital.model
//define la estructura de los datos que se envie y recibe la info
// Esta es la nota donde el cliente escribe su nombre y contraseña para entrar
// Objeto para la petición de Login
data class LoginRequest(
    val username: String,
    val password: String
)
// Este es el pase de entrada que nos regresa el servidor si la clave fue correcta
// Objeto para la respuesta de Login
data class LoginResponse(
    val token: String
)

// Objeto para representar al Usuario de la API
// Esta es la credencial con los datos completos de la persona
data class UserData(
    val id: Int,
    val username: String
)