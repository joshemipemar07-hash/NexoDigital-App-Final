package com.example.nexodigital.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nexodigital.model.ProductModel
import com.example.nexodigital.model.ProductRequest
import com.example.nexodigital.repository.ProductRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Eventos de interfaz para notificar éxito o error al crear un producto (US06)
sealed class ProductUiEvent {
    data class ShowSuccess(val newId: Int) : ProductUiEvent()
    data class ShowError(val message: String) : ProductUiEvent()
}

class ProductController(
    private val repository: ProductRepository
) : ViewModel() {

    // Variables de estado observables vinculadas a la interfaz de usuario mediante Jetpack Compose
    var products by mutableStateOf<List<ProductModel>>(value = emptyList())
    var categories by mutableStateOf<List<String>>(value = emptyList())
    var selectedCategory by mutableStateOf<String?>(value = null)
    var isLoading by mutableStateOf(value = false)
    var errorMessage by mutableStateOf<String?>(value = null)

    // ==========================================
    // NUEVAS VARIABLES Y LÓGICA PARA LA US06 (Agregar Producto)
    // ==========================================
    var title by mutableStateOf("")
    var price by mutableStateOf("")
    var description by mutableStateOf("")
    var imageUrl by mutableStateOf("")
    var category by mutableStateOf("")

    // Estados de error para resaltar los campos en rojo (Escenario 2)
    var titleError by mutableStateOf(false)
    var priceError by mutableStateOf(false)
    var descriptionError by mutableStateOf(false)
    var imageError by mutableStateOf(false)
    var categoryError by mutableStateOf(false)

    private val _uiEvent = MutableSharedFlow<ProductUiEvent>()
    val uiEvent: SharedFlow<ProductUiEvent> = _uiEvent

    /**
     * Carga la lista general de productos desde el servidor.
     */
    fun loadProducts() {
        isLoading = true
        errorMessage = null

        // Ejecuta la consulta de red en un hilo secundario
        CoroutineScope(context = Dispatchers.IO).launch {
            val result = repository.getProducts()

            // Regresa al hilo principal para actualizar los estados reactivos
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

    /**
     * Carga la lista de categorías disponibles para el catálogo.
     */
    fun loadCategories() {
        CoroutineScope(context = Dispatchers.IO).launch {
            val result = repository.getCategories()
            withContext(Dispatchers.Main) {
                if (result.isSuccess) {
                    categories = result.getOrNull() ?: emptyList()
                }
            }
        }
    }

    /**
     * Filtra los productos del catálogo según la categoría seleccionada por el usuario.
     */
    fun filterByCategory(category: String) {
        selectedCategory = category
        isLoading = true
        errorMessage = null
        products = emptyList()

        CoroutineScope(context = Dispatchers.IO).launch {
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

    /**
     * Restablece los filtros aplicados, limpiando la categoría seleccionada y recargando todo el catálogo general.
     */
    fun resetFilter() {
        selectedCategory = null
        products = emptyList()
        loadProducts()
    }

    /**
     * Valida localmente el formulario y envía el nuevo producto mediante el repositorio (US06).
     */
    fun submitProduct() {
        // Validaciones locales estrictas (Escenario 2)
        val isTitleEmpty = title.isBlank()
        val parsedPrice = price.toDoubleOrNull()
        val isPriceInvalid = parsedPrice == null || parsedPrice <= 0.0
        val isDescEmpty = description.isBlank()
        val isImageEmpty = imageUrl.isBlank()
        val isCategoryEmpty = category.isBlank()

        titleError = isTitleEmpty
        priceError = isPriceInvalid
        descriptionError = isDescEmpty
        imageError = isImageEmpty
        categoryError = isCategoryEmpty

        // Si hay errores, se detiene la petición de red localmente
        if (isTitleEmpty || isPriceInvalid || isDescEmpty || isImageEmpty || isCategoryEmpty) {
            return
        }

        viewModelScope.launch {
            try {
                val request = ProductRequest(
                    title = title,
                    price = parsedPrice!!,
                    description = description,
                    image = imageUrl,
                    category = category
                )

                // Llamada al repositorio para enviar el producto
                val result = repository.addProduct(request)

                if (result.isSuccess) {
                    // Éxito: Muestra confirmación y limpia el formulario
                    _uiEvent.emit(value = ProductUiEvent.ShowSuccess(newId = 0))
                    clearForm()
                } else {
                    _uiEvent.emit(value = ProductUiEvent.ShowError("Error al registrar el producto en la API"))
                }
            } catch (e: Exception) {
                _uiEvent.emit(ProductUiEvent.ShowError("Excepción de red: ${e.localizedMessage}"))
            }
        }
    }

    private fun clearForm() {
        title = ""
        price = ""
        description = ""
        imageUrl = ""
        category = ""
        titleError = false
        priceError = false
        descriptionError = false
        imageError = false
        categoryError = false
    }
}