package com.example.nexodigital.model

/**
 * Modelo de Datos de Producto (ProductModel)
 * Define la estructura de datos (data class) que representa un producto dentro de la aplicación.
 * Funciona como objeto de transferencia y almacenamiento para deserializar las respuestas
 */
data class ProductModel(

    val id: Int, // Identificador del producto
    val title: String, // Título o nombre del producto
    val price: Double, // Costo o precio del producto
    val description: String, // Descripción las características del producto
    val category: String, // Categoría a la que pertenece el producto dentro del catálogo
    val image: String // URL o ruta de acceso a la imagen del producto
)