package com.example.nexodigital.model

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE


/**
 * Interfaz de Servicios de la API (FakeStoreApiService)
 * Definimos la comunicación HTTP mediante anotaciones de Retrofit
 * (con @GET y @POST), especificamos el manejo
 * de solicitudes de autenticación, usuarios y gestión del catálogo de productos.
 */
interface FakeStoreApiService {

    /**
     * Envía las credenciales del usuario para procesar el inicio de sesión.
     */
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    /**
     * Obtiene la lista general de usuarios registrados en el sistema.
     */
    @GET("users")
    suspend fun getUsers(): Response<List<UserData>>

    /**
     * Obtiene el catálogo completo de productos disponibles.
     */
    @GET("products")
    suspend fun getProducts(): Response<List<ProductModel>>

    /**
     * Obtiene la lista de categorías disponibles para el filtrado de productos.
     */
    @GET("products/categories")
    suspend fun getCategories(): Response<List<String>>

    /**
     * Obtiene la lista de productos filtrados por una categoría específica.
     */
    @GET("products/category/{category}")
    suspend fun getProductsByCategory(@Path("category") category: String): Response<List<ProductModel>>

    /**
     * Obtiene la información detallada de un producto específico según su ID.
     */
    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: Int): Response<ProductModel>

    /**
     * Envía un nuevo producto al catálogo mediante una petición POST.
     */
    @POST("products")
    suspend fun addProduct(@Body product: ProductRequest): Response<ProductResponse>

    /**
     * Actualiza la información de un producto existente mediante una petición PUT.
     */
    @PUT("products/{id}")
    suspend fun updateProduct(
        @Path("id") id: Int,
        @Body product: ProductRequest
    ): Response<ProductResponse>

    /**
     * Elimina un producto existente mediante una petición DELETE (US08).
     */
    @DELETE("products/{id}")
    suspend fun deleteProduct(@Path("id") id: Int): Response<ProductModel>
}

/**
 * configura y provee una única inicialisacion de Retrofit, estableciendo la URL base y el convertidor
 * JSON para la app.
 */
object RetrofitInstance {
    // URL base correspondiente a los servicios de la API
    private const val BASE_URL = "https://fakestoreapi.com/"

    val api: FakeStoreApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FakeStoreApiService::class.java)
    }
}