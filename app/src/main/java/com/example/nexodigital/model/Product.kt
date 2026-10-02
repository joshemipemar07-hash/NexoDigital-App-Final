package com.example.nexodigital.model // (Nota: Asegúrate de que el package coincida con el de tu proyecto)

/**
 * Modelo utilizado para ENVIAR los datos del nuevo producto a la API (POST /products).
 */
data class ProductRequest(
    val title: String,
    val price: Double,
    val description: String,
    val image: String,
    val category: String
)

/**
 * Modelo utilizado para RECIBIR la respuesta del servidor cuando el producto es creado con éxito.
 * Incluye el 'id' generado por la API.
 */
data class ProductResponse(
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val image: String,
    val category: String
)