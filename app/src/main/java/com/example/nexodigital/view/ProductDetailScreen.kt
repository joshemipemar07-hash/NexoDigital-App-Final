package com.example.nexodigital.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    LaunchedEffect(productId) {
        controller.loadProductDetail(productId)
    }

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Artículo") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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

                        // Exclusión estricta de jerarquía visual si no es Administrador (Regla US05)
                        if (controller.isAdmin) {
                            Spacer(modifier = Modifier.height(24.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Button(
                                    onClick = { /* Acción de edición */ },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Editar")
                                }
                                OutlinedButton(
                                    onClick = { /* Acción de eliminación */ },
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