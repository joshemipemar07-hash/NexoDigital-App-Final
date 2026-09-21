package com.example.nexodigital.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.nexodigital.model.ProductModel
import com.example.nexodigital.repository.AuthRepository
import com.example.nexodigital.repository.ProductRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductDetailController(
    private val productRepository: ProductRepository,
    private val authRepository: AuthRepository
) {
    var product by mutableStateOf<ProductModel?>(null)
    var isLoading by mutableStateOf(false)
    var isUnavailableAlertVisible by mutableStateOf(false)

    val userRole: String
        get() = authRepository.getUserRole()

    val isAdmin: Boolean
        get() = userRole.equals("Administrador", ignoreCase = true) || userRole.equals("Admin", ignoreCase = true)

    fun loadProductDetail(id: Int) {
        isLoading = true
        isUnavailableAlertVisible = false
        CoroutineScope(Dispatchers.IO).launch {
            val result = productRepository.getProductById(id)
            withContext(Dispatchers.Main) {
                isLoading = false
                if (result.isSuccess) {
                    val body = result.getOrNull()
                    if (body != null) {
                        product = body
                    } else {
                        isUnavailableAlertVisible = true
                    }
                } else {
                    product = null
                    isUnavailableAlertVisible = true
                }
            }
        }
    }
}