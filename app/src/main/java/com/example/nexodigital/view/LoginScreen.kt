package com.example.nexodigital.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.nexodigital.controller.AuthController

@Composable
fun LoginScreen(
    controller: AuthController,
    onLoginSuccess: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Nexo Digital", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = controller.username,
            onValueChange = { newValue: String -> controller.username = newValue },
            label = { Text("Usuario") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = controller.password,
            onValueChange = { newValue: String -> controller.password = newValue },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (controller.isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = { controller.performLogin(onLoginSuccess) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ingresar")
            }
        }

        val currentError: String? = controller.errorMessage
        if (!currentError.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = currentError, color = MaterialTheme.colorScheme.error)
        }
    }
}