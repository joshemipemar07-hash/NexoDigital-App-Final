package com.example.nexodigital.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.nexodigital.model.ProductModel
import com.example.nexodigital.repository.ProductRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductController(
    private val repository: ProductRepository
) {
    var products by mutableStateOf<List<ProductModel>>(emptyList())
    var categories by mutableStateOf<List<String>>(emptyList())
    var selectedCategory by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun loadProducts() {
        isLoading = true
        errorMessage = null
        CoroutineScope(Dispatchers.IO).launch {
            val result = repository.getProducts()
            withContext(Dispatchers.Main) {
                isLoading = false
                if (result.isSuccess) {
                    products = result.getOrNull() ?: emptyList()
                } else {
                    errorMessage = result.exceptionOrNull()?.message
                }
            }
        }
    }

    fun loadCategories() {
        CoroutineScope(Dispatchers.IO).launch {
            val result = repository.getCategories()
            withContext(Dispatchers.Main) {
                if (result.isSuccess) {
                    categories = result.getOrNull() ?: emptyList()
                }
            }
        }
    }

    fun filterByCategory(category: String) {
        selectedCategory = category
        isLoading = true
        errorMessage = null
        products = emptyList() // Limpia memoria local preventiva [cite: 15]

        CoroutineScope(Dispatchers.IO).launch {
            val result = repository.getProductsByCategory(category)
            withContext(Dispatchers.Main) {
                isLoading = false
                if (result.isSuccess) {
                    products = result.getOrNull() ?: emptyList()
                } else {
                    errorMessage = result.exceptionOrNull()?.message
                }
            }
        }
    }

    fun resetFilter() {
        selectedCategory = null
        products = emptyList() // Limpia memoria antes de recargar todo lo general
        loadProducts()
    }
}