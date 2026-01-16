package com.mindcard.data.repository

import com.mindcard.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthRepository {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    suspend fun signInWithGoogle(): Result<User> {
        // Simulação de login
        val mockUser = User("1", "Usuário Teste", "teste@mindcard.com")
        _currentUser.value = mockUser
        return Result.success(mockUser)
    }

    fun signOut() {
        _currentUser.value = null
    }
}
