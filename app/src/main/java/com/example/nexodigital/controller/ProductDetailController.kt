package com.example.nexodigital.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.nexodigital.model.ProductModel
import com.example.nexodigital.model.ProductRequest
import com.example.nexodigital.repository.AuthRepository
import com.example.nexodigital.repository.ProductRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Controlador de Detalles de Producto (ProductDetailController)
 * Gestiona la lógica de negocio y el estado para mostrar la información detallada
 * de un producto específico, validando además los permisos o roles del usuario autenticado
 * (como verificar si cuenta con privilegios de administrador) para habilitar o restringir acciones en la vista.
 */
class ProductDetailController(
    private val productRepository: ProductRepository,
    private val authRepository: AuthRepository
) {
    // Variables de estado observables vinculadas a la interfaz de usuario mediante Jetpack Compose

    // Almacena el modelo del producto consultado (nulo si aún no se carga o no existe)
    var product by mutableStateOf<ProductModel?>(null)

    // Indicador booleano para controlar el estado de carga visual en la pantalla de detalles
    var isLoading by mutableStateOf(false)

    // Controla la visibilidad de una alerta o aviso en caso de que el producto no esté disponible
    var isUnavailableAlertVisible by mutableStateOf(false)

    // Variable para controlar la visibilidad del diálogo de confirmación de eliminación (Escenario 2)
    var isDeleteDialogVisible by mutableStateOf(false)

    // Variables para los campos del formulario de edición (US07)
    var title by mutableStateOf("")
    var price by mutableStateOf("")
    var description by mutableStateOf("")
    var imageUrl by mutableStateOf("")
    var category by mutableStateOf("")

    // Obtenemos de forma dinámica el rol actual del usuario desde el repositorio de autenticación
    val userRole: String
        get() = authRepository.getUserRole()

    // Propiedad calculada que determina si el usuario actual posee permisos de administrador
    val isAdmin: Boolean
        get() = userRole.equals("Administrador", ignoreCase = true) || userRole.equals("Admin", ignoreCase = true)

    /**
     * Carga la información detallada de un producto a partir de su identificador único.
     */
    fun loadProductDetail(id: Int) {
        isLoading = true
        isUnavailableAlertVisible = false

        // Ejecuta la consulta de red en un hilo secundario para evitar bloqueos
        CoroutineScope(context = Dispatchers.IO).launch {
            val result = productRepository.getProductById(id)

            // Regresa al hilo principal para actualizar los estados de la interfaz
            withContext(Dispatchers.Main) {
                isLoading = false
                if (result.isSuccess) {
                    val body = result.getOrNull()
                    if (body != null) {
                        product = body // Asigna el producto encontrado
                    } else {
                        isUnavailableAlertVisible = true // Activa la alerta si el cuerpo de la respuesta es nulo
                    }
                } else {
                    product = null
                    isUnavailableAlertVisible = true // Activa la alerta si la petición de red falla
                }
            }
        }
    }

    /**
     * Precarga la información actual del producto en los campos de edición (Escenario 2).
     */
    fun loadProductDataForEditing(productModel: ProductModel) {
        title = productModel.title
        price = productModel.price.toString()
        description = productModel.description
        imageUrl = productModel.image
        category = productModel.category
    }

    /**
     * Envía los datos modificados al servidor mediante una petición PUT (Escenario 1).
     */
    fun saveProductChanges(productId: Int, onSuccess: () -> Unit) {
        if (title.isBlank() || price.isBlank() || description.isBlank() || imageUrl.isBlank() || category.isBlank()) {
            return
        }

        val priceDouble = price.toDoubleOrNull()
        if (priceDouble == null || priceDouble <= 0) {
            return
        }

        isLoading = true
        CoroutineScope(Dispatchers.IO).launch {
            val request = ProductRequest(
                title = title,
                price = priceDouble,
                description = description,
                image = imageUrl,
                category = category
            )

            val result = productRepository.updateProduct(productId, request)

            withContext(Dispatchers.Main) {
                isLoading = false
                result.onSuccess {
                    onSuccess() // Cierra el formulario o recarga al tener éxito
                }.onFailure {
                    // Manejo silencioso o de error
                }
            }
        }
    }

    /**
     * Ejecuta la petición DELETE para remover el producto (Escenario 1).
     */
    fun deleteProduct(productId: Int, onSuccess: () -> Unit) {
        isLoading = true
        CoroutineScope(Dispatchers.IO).launch {
            val result = productRepository.deleteProduct(productId)

            withContext(Dispatchers.Main) {
                isLoading = false
                result.onSuccess {
                    onSuccess() // Redirige al catálogo general al tener éxito
                }.onFailure {
                    // Manejo de error de red si ocurre
                }
            }
        }
    }
}