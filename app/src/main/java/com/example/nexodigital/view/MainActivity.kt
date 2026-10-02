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

/**
 * Actividad Principal (MainActivity)
 * Actúa como el punto de entrada de la aplicación Android. Configuramos
 * (capas de red, repositorios y controladores) y administramos el enrutamiento
 * de la interfaz de usuario con Jetpack Compose en función del estado de sesión del usuario y la navegación activa.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicialización de dependencias base (Singleton de red, gestión de sesiones y repositorios)
        val apiService = RetrofitInstance.api
        val sessionManager = SessionManager(applicationContext)
        val productRepository = ProductRepository(apiService)
        val authRepository = AuthRepository(apiService, sessionManager)

        // Inicialización de controladores de presentación
        val authController = AuthController(authRepository)
        val productController = ProductController(productRepository)
        val productDetailController = ProductDetailController(productRepository, authRepository)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    // Estados reactivos para controlar la sesión y las vistas activas
                    var isLoggedIn by remember { mutableStateOf(authRepository.isSessionActive()) }
                    var selectedProductId by remember { mutableStateOf<Int?>(null) }
                    var isAddingProduct by remember { mutableStateOf(false) } // <--- NUEVO ESTADO

                    if (!isLoggedIn) {
                        // Despliega la pantalla de inicio de sesión si no hay una sesión activa
                        LoginScreen(
                            controller = authController,
                            onLoginSuccess = {
                                isLoggedIn = true
                            }
                        )
                    } else {
                        val currentProductId = selectedProductId

                        when {
                            // 1. Si está activo el modo de añadir producto, mostramos su pantalla
                            isAddingProduct -> {
                                AddProductScreen(
                                    controller = productController,
                                    onProductCreated = {
                                        // Regresa al Home y recarga la lista de productos al crear uno nuevo con éxito
                                        isAddingProduct = false
                                        productController.loadProducts()
                                    }
                                )
                            }
                            // 2. Si se seleccionó un producto, mostramos los detalles
                            currentProductId != null -> {
                                ProductDetailScreen(
                                    productId = currentProductId,
                                    controller = productDetailController,
                                    onBack = {
                                        selectedProductId = null
                                    }
                                )
                            }
                            // 3. Pantalla principal por defecto (Home con catálogo)
                            else -> {
                                HomeScreen(
                                    authController = authController,
                                    productController = productController,
                                    onProductClick = { productId ->
                                        selectedProductId = productId
                                    },
                                    onAddProductClick = {
                                        isAddingProduct = true // <--- CAMBIA EL ESTADO AL PRESIONAR EL FAB (+)
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
}