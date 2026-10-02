package com.example.nexodigital.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.nexodigital.controller.ProductDetailController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: Int,
    controller: ProductDetailController,
    onBack: () -> Unit
) {
    // Estado local para alternar entre la vista de detalle y el formulario de edición
    var isEditing by remember { mutableStateOf(false) }

    LaunchedEffect(productId) {
        controller.loadProductDetail(productId)
    }

    // 1. Alerta de producto no disponible (fuera de otros diálogos)
    if (controller.isUnavailableAlertVisible) {
        AlertDialog(
            onDismissRequest = { onBack() },
            confirmButton = {
                TextButton(onClick = { onBack() }) {
                    Text("Aceptar")
                }
            },
            title = { Text("Aviso") },
            text = { Text("Producto no disponible") }
        )
    }

    // 2. Cuadro de diálogo de confirmación para la eliminación (US08)[cite: 7]
    if (controller.isDeleteDialogVisible) {
        AlertDialog(
            onDismissRequest = { controller.isDeleteDialogVisible = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de eliminar este producto?") }, //[cite: 7]
            confirmButton = {
                TextButton(
                    onClick = {
                        controller.isDeleteDialogVisible = false
                        // Ejecuta la petición DELETE y regresa al catálogo (Escenario 1)[cite: 7]
                        controller.deleteProduct(productId) {
                            onBack()
                        }
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        // Escenario 2: Cancela el borrado sin hacer peticiones de red[cite: 7]
                        controller.isDeleteDialogVisible = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Editar Artículo" else "Detalle del Artículo") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isEditing) {
                            isEditing = false // Si está editando, regresa a la vista de detalle
                        } else {
                            onBack() // Si está en detalle, sale de la pantalla
                        }
                    }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (controller.isLoading) {
                CircularProgressIndicator()
            } else {
                val currentProduct = controller.product
                if (currentProduct != null) {
                    if (isEditing) {
                        // --- ESCENARIO 2: Formulario de edición pre-cargado ---
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            OutlinedTextField(
                                value = controller.title,
                                onValueChange = { controller.title = it },
                                label = { Text("Título del producto") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = controller.price,
                                onValueChange = { controller.price = it },
                                label = { Text("Precio") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = controller.description,
                                onValueChange = { controller.description = it },
                                label = { Text("Descripción") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = controller.imageUrl,
                                onValueChange = { controller.imageUrl = it },
                                label = { Text("URL de la imagen") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = controller.category,
                                onValueChange = { controller.category = it },
                                label = { Text("Categoría") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = {
                                    // Ejecuta la petición PUT a la API (Escenario 1)
                                    controller.saveProductChanges(productId) {
                                        isEditing = false
                                        controller.loadProductDetail(productId) // Recarga los datos actualizados
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Guardar Cambios (Simulación)")
                            }
                        }
                    } else {
                        // --- Vista normal de Detalles ---
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AsyncImage(
                                model = currentProduct.image,
                                contentDescription = currentProduct.title,
                                modifier = Modifier
                                    .height(220.dp)
                                    .fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = currentProduct.title,
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    text = currentProduct.category.replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.labelLarge,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "$${currentProduct.price}",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = currentProduct.description,
                                style = MaterialTheme.typography.bodyLarge
                            )

                            // Restricción de permisos (Escenario 3 / US05): Solo visible para Administradores (`mor_2314`)
                            if (controller.isAdmin) {
                                Spacer(modifier = Modifier.height(24.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            // 1. Precargamos los datos actuales en el controlador
                                            controller.loadProductDataForEditing(currentProduct)
                                            // 2. Activamos la pantalla/formulario de edición (Escenario 2)
                                            isEditing = true
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Editar")
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            // Escenario 2 (US08): Muestra el cuadro de diálogo de confirmación obligatoria[cite: 7]
                                            controller.isDeleteDialogVisible = true
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Eliminar")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}