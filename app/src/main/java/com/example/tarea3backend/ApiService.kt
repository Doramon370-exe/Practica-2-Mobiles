package com.example.tarea3backend

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import com.google.gson.annotations.SerializedName
import retrofit2.http.DELETE
import retrofit2.http.PUT
import retrofit2.http.Path

// Estructura del JSON que se envia a Node.js
data class UserRequest(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)

// Estructura del JSON que se recibe de Node.js
data class AuthResponse(
    @SerializedName("message") val message: String
)

data class Nota(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("contenido") val contenido: String
)
interface ApiService {
    @GET("/")
    suspend fun verificarApi(): Response<ResponseBody>

    @POST("/register")
    suspend fun registrarUsuario(@Body user: UserRequest): Response<AuthResponse>

    @POST("/login")
    suspend fun loginUsuario(@Body user: UserRequest): Response<AuthResponse>

    @GET("/notas")
    suspend fun obtenerNotas(): Response<List<Nota>>

    @POST("/notas")
    suspend fun crearNota(@Body nota: Nota): Response<Nota>

    @PUT("/notas/{id}")
    suspend fun actualizarNota(@Path("id") id: Int, @Body nota: Nota): Response<Nota>

    @DELETE("/notas/{id}")
    suspend fun eliminarNota(@Path("id") id: Int): retrofit2.Response<okhttp3.ResponseBody>

}