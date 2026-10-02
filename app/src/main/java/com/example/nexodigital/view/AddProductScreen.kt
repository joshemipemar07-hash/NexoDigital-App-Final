package com.example.nexodigital.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.nexodigital.controller.ProductController
import com.example.nexodigital.controller.ProductUiEvent
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    controller: ProductController,
    onProductCreated: () -> Unit // Callback para regresar o navegar al éxito
) {
    val snackbarHostState = remember { SnackbarHostState() }

    // Escuchar los eventos de éxito o error enviados por el controlador
    LaunchedEffect(key1 = true) {
        controller.uiEvent.collectLatest { event ->
            when (event) {
                is ProductUiEvent.ShowSuccess -> {
                    snackbarHostState.showSnackbar("¡Producto registrado con éxito! ID: ${event.newId}")
                    onProductCreated()
                }
                is ProductUiEvent.ShowError -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Registrar Nuevo Producto") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Campo Título
            OutlinedTextField(
                value = controller.title,
                onValueChange = { controller.title = it },
                label = { Text("Título del producto") },
                isError = controller.titleError,
                modifier = Modifier.fillMaxWidth()
            )
            if (controller.titleError) {
                Text("El título no puede estar vacío", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            // Campo Precio
            OutlinedTextField(
                value = controller.price,
                onValueChange = { controller.price = it },
                label = { Text("Precio") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = controller.priceError,
                modifier = Modifier.fillMaxWidth()
            )
            if (controller.priceError) {
                Text("Ingrese un precio válido mayor a 0", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            // Campo Descripción
            OutlinedTextField(
                value = controller.description,
                onValueChange = { controller.description = it },
                label = { Text("Descripción") },
                isError = controller.descriptionError,
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            if (controller.descriptionError) {
                Text("La descripción es obligatoria", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            // Campo Imagen URL
            OutlinedTextField(
                value = controller.imageUrl,
                onValueChange = { controller.imageUrl = it },
                label = { Text("URL de la imagen") },
                isError = controller.imageError,
                modifier = Modifier.fillMaxWidth()
            )
            if (controller.imageError) {
                Text("La URL de la imagen es obligatoria", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            // Campo Categoría
            OutlinedTextField(
                value = controller.category,
                onValueChange = { controller.category = it },
                label = { Text("Categoría") },
                isError = controller.categoryError,
                modifier = Modifier.fillMaxWidth()
            )
            if (controller.categoryError) {
                Text("La categoría es obligatoria", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de Enviar / Guardar
            Button(
                onClick = { controller.submitProduct() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Producto")
            }
        }
    }
}