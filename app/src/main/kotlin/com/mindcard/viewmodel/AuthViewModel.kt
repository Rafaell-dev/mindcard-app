package com.mindcard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindcard.data.model.User
import com.mindcard.data.repository.AuthRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    val currentUser: StateFlow<User?> = repository.currentUser

    fun signIn(onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.signInWithGoogle().onSuccess {
                onSuccess()
            }
        }
    }
}
