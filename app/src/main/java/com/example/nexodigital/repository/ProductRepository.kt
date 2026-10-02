package com.example.nexodigital.repository

import com.example.nexodigital.model.FakeStoreApiService
import com.example.nexodigital.model.ProductModel
import com.example.nexodigital.model.ProductRequest

/**
 * Repositorio de Productos (ProductRepository)
 * Actúa como la fuente para la gestión de datos del catálogo de productos,
 * realizando las peticiones de red a través de Retrofit (FakeStoreApiService) y manejando
 * los resultados
 */
class ProductRepository(
    private val apiService: FakeStoreApiService
) {
    /**
     * Obtiene de manera la lista general de productos desde la API.
     */
    suspend fun getProducts(): Result<List<ProductModel>> {
        return try {
            val response = apiService.getProducts()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al cargar productos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene de manera la lista de categorías disponibles para el filtrado del catálogo.
     */
    suspend fun getCategories(): Result<List<String>> {
        return try {
            val response = apiService.getCategories()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al obtener categorías"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene los productos pertenecientes a una categoría específica.
     * el nombre de la categoría a consultar.
     */
    suspend fun getProductsByCategory(category: String): Result<List<ProductModel>> {
        return try {
            val response = apiService.getProductsByCategory(category)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al filtrar por categoría"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene la información detallada de un producto específico mediante su ID.
     * obtenemossuIdentificador numérico único del producto.
     */
    suspend fun getProductById(id: Int): Result<ProductModel> {
        return try {
            val response = apiService.getProductById(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Producto no disponible"))
            }
        } catch (_: Exception) {
            Result.failure(Exception("Producto no disponible"))
        }
    }

    /**
     * Envía un nuevo producto a la API para su registro.
     */
    suspend fun addProduct(request: ProductRequest): Result<Any> {
        return try {
            val response = apiService.addProduct(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(value = response.body()!!)
            } else {
                Result.failure(Exception("Error al registrar el producto"))
            }
        } catch (e: Exception) {
            Result.failure(exception = e)
        }
    }
    /**
     * Envía los datos actualizados de un producto a la API mediante PUT.
     */
    suspend fun updateProduct(id: Int, request: ProductRequest): Result<Any> {
        return try {
            val response = apiService.updateProduct(id, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al actualizar el producto"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Solicita la eliminación de un producto al servidor mediante DELETE[cite: 6].
     */
    suspend fun deleteProduct(id: Int): Result<Any> {
        return try {
            val response = apiService.deleteProduct(id)
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Error al eliminar el producto"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    }
