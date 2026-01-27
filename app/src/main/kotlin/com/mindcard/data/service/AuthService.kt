package com.mindcard.data.service

import com.mindcard.BuildConfig
import com.mindcard.data.model.User
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(val email: String, val senha: String)
data class RegisterRequest(val email: String, val senha: String, val nome: String)

data class LoginResponse(
    val accessToken: String? = null,
    val user: User? = null
)

data class RegisterResponse (
    val user: User? = null
)

data class ErrorResponse(
    val message: String
)

interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("usuario/cadastrar")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>
}

class AuthService {
    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(AuthApi::class.java)

    suspend fun login(email: String, senha: String): Response<LoginResponse> {
        return api.login(LoginRequest(email, senha))
    }

    suspend fun register(email: String, senha: String, nome: String): Response<RegisterResponse> {
        return api.register(RegisterRequest(email, senha, nome))
    }
}
