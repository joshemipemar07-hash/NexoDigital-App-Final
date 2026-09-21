package com.example.nexodigital.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.nexodigital.controller.AuthController
import com.example.nexodigital.controller.ProductController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    authController: AuthController,
    productController: ProductController,
    onProductClick: (Int) -> Unit,
    onLogout: () -> Unit
) {
    // Inicializa la carga de productos y categorías al abrir la vista
    LaunchedEffect(Unit) {
        productController.loadProducts()
        productController.loadCategories()
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // aqui damos la bienvenida
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Catálogo - ${authController.getRole()}", style = MaterialTheme.typography.titleLarge)
            Button(onClick = onLogout) {
                Text("Salir")
            }
        }

        // Barra de filtros para las categorías
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = productController.selectedCategory == null,
                    onClick = { productController.resetFilter() },
                    label = { Text("Todos") }
                )
            }
            items(productController.categories) { categoryName ->
                FilterChip(
                    selected = productController.selectedCategory == categoryName,
                    onClick = { productController.filterByCategory(categoryName) },
                    label = { Text(categoryName.replaceFirstChar { it.uppercase() }) }
                )
            }
        }

        // Indicador de carga visual (Spinner)
        if (productController.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (productController.errorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = productController.errorMessage!!, color = MaterialTheme.colorScheme.error)
            }
        } else {
            // Lista de productos
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(productController.products) { product ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onProductClick(product.id) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = product.image,
                                contentDescription = product.title,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(product.title, style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("$${product.price}", style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }
                }
            }
        }
    }
}