package com.example.nexodigital

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Instanciación de Objetos (Arquitectura POO)
        val sessionManager = SessionManager(this)
        val repository = AuthRepository(RetrofitInstance.api, sessionManager)
        val controller = AuthController(repository)

        setContent {
            // Manejo de Estado de la Pantalla Actual
            var currentScreen by remember {
                mutableStateOf(if (repository.isSessionActive()) "HOME" else "LOGIN")
            }

            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (currentScreen == "LOGIN") {
                        LoginScreen(
                            controller = controller,
                            onLoginSuccess = { currentScreen = "HOME" } // Redirige a HOME
                        )
                    } else {
                        HomeScreen(
                            controller = controller,
                            onLogout = { currentScreen = "LOGIN" } // Redirige a LOGIN
                        )
                    }
                }
            }
        }
    }
}

// Escenario 3 (US01): Manejo de conectividad de red
fun isInternetAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
    return activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

@Composable
fun LoginScreen(controller: AuthController, onLoginSuccess: () -> Unit) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Nexo Tech", fontSize = 32.sp, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Iniciar Sesión", fontSize = 18.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = controller.username,
            onValueChange = { controller.username = it },
            label = { Text("Usuario") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = controller.password,
            onValueChange = { controller.password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Escenario 2 (US01): Muestra alerta de credenciales inválidas o falta de internet
        controller.errorMessage?.let {
            Text(text = it, color = Color.Red, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(12.dp))
        }

        Button(
            onClick = {
                // Ejecuta la autenticación y al completar navega a HOME
                controller.performLogin(context) {
                    onLoginSuccess()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !controller.isLoading
        ) {
            Text(if (controller.isLoading) "Cargando..." else "Ingresar")
        }
    }
}

@Composable
fun HomeScreen(controller: AuthController, onLogout: () -> Unit) {
    val role = controller.getRole()

    // US02 Escenario 3: Carrito local inicializado
    var cartCount by remember { mutableIntStateOf(3) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Bienvenido a Nexo Tech", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(12.dp))

        // US01 Escenario 1: Mostrar rol mapeado desde la API
        Text("Rol asignado: $role", fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(12.dp))

        Text("Productos en Carrito: $cartCount", fontSize = 16.sp)

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                // US02 Escenarios 1 y 3: Reseteo de estado global, eliminación de datos y logout
                cartCount = 0
                controller.performLogout {
                    onLogout()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("Cerrar Sesión", color = Color.White)
        }
    }
}