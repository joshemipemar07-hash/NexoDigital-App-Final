package com.example.nexodigital.model

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.POST

interface FakeStoreApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("users")
    suspend fun getUsers(): Response<List<UserData>>

    @GET("products")
    suspend fun getProducts(): Response<List<ProductModel>>

    @GET("products/categories")
    suspend fun getCategories(): Response<List<String>>

    @GET("products/category/{category}")
    suspend fun getProductsByCategory(@Path("category") category: String): Response<List<ProductModel>>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: Int): Response<ProductModel>
}

object RetrofitInstance {
    private const val BASE_URL = "https://fakestoreapi.com/"

    val api: FakeStoreApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FakeStoreApiService::class.java)
    }
}