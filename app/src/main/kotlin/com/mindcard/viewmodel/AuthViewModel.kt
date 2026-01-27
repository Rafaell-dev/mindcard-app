package com.mindcard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindcard.data.model.User
import com.mindcard.data.service.AuthService
import com.mindcard.data.service.ErrorResponse
import com.google.gson.Gson
import com.mindcard.data.local.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}



class AuthViewModel(
    private val authService: AuthService,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        // Tenta recuperar o usuário da sessão
        val savedUser = sessionManager.fetchUser()
        val savedToken = sessionManager.fetchAuthToken()

        if (savedUser != null && savedToken != null) {
            _currentUser.value = savedUser
            _authState.value = AuthState.Success(savedUser)
        }
    }

    fun login(email: String, senha: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = authService.login(email, senha)
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()?.user
                    val accessToken = response.body()?.accessToken
                    
                    if (accessToken != null) {
                        sessionManager.saveAuthToken(accessToken)
                    }

                    if (user != null) {
                        sessionManager.saveUser(user)
                    }

                    _currentUser.value = user
                    if (user != null) {
                         _authState.value = AuthState.Success(user)
                    } else {
                         _authState.value = AuthState.Error("Erro: Usuário não retornado")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val message = try {
                        val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                        errorResponse.message
                    } catch (e: Exception) {
                        "Erro no login: ${response.code()}"
                    }
                    _authState.value = AuthState.Error(message)
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Erro ao entrar")
            }
        }
    }

    fun register(nome: String, email: String, senha: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = authService.register(email, senha, nome)
                if (response.isSuccessful && response.body() != null) {
                    // Registration successful, now auto-login
                    login(email, senha)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val message = try {
                        val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                        errorResponse.message
                    } catch (e: Exception) {
                        "Erro no cadastro: ${response.code()}"
                    }
                    _authState.value = AuthState.Error(message)
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Erro ao cadastrar")
            }
        }
    }

    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }
    
    fun logout() {
        sessionManager.clearAuthToken()
        _currentUser.value = null
        _authState.value = AuthState.Idle
    }
}
