package com.example.nexodigital.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.nexodigital.controller.AuthController
import com.example.nexodigital.controller.ProductController
import com.example.nexodigital.controller.ProductDetailController
import com.example.nexodigital.model.RetrofitInstance
import com.example.nexodigital.model.SessionManager
import com.example.nexodigital.repository.AuthRepository
import com.example.nexodigital.repository.ProductRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apiService = RetrofitInstance.api
        val sessionManager = SessionManager(applicationContext)
        val productRepository = ProductRepository(apiService)
        val authRepository = AuthRepository(apiService, sessionManager)

        val authController = AuthController(authRepository)
        val productController = ProductController(productRepository)
        val productDetailController = ProductDetailController(productRepository, authRepository)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    var isLoggedIn by remember { mutableStateOf(authRepository.isSessionActive()) }
                    var selectedProductId by remember { mutableStateOf<Int?>(null) }

                    if (!isLoggedIn) {
                        LoginScreen(
                            controller = authController,
                            onLoginSuccess = {
                                isLoggedIn = true
                            }
                        )
                    } else {
                        val currentProductId = selectedProductId
                        if (currentProductId != null) {
                            ProductDetailScreen(
                                productId = currentProductId,
                                controller = productDetailController,
                                onBack = {
                                    selectedProductId = null
                                }
                            )
                        } else {
                            HomeScreen(
                                authController = authController,
                                productController = productController,
                                onProductClick = { productId ->
                                    selectedProductId = productId
                                },
                                onLogout = {
                                    authController.performLogout {
                                        isLoggedIn = false
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}