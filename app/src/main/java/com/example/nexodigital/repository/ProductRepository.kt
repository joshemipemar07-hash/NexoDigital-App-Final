package com.example.nexodigital.repository

import com.example.nexodigital.model.FakeStoreApiService
import com.example.nexodigital.model.ProductModel

class ProductRepository(
    private val apiService: FakeStoreApiService
) {
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
}