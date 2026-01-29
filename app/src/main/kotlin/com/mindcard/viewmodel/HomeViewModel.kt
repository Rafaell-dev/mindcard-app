package com.mindcard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mindcard.data.model.Mindcard
import com.mindcard.data.repository.MindcardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class MindcardState {
    object Idle : MindcardState()
    object Loading : MindcardState()
    data class Success(val mindcards: List<Mindcard>) : MindcardState()
    data class Error(val message: String) : MindcardState()
}

class HomeViewModel(private val mindcardRepository: MindcardRepository) : ViewModel() {

    private val _mindcards = MutableStateFlow<List<Mindcard>>(emptyList())
    val mindcards: StateFlow<List<Mindcard>> = _mindcards.asStateFlow()

    private val _mindcardState = MutableStateFlow<MindcardState>(MindcardState.Idle)
    val mindcardState: StateFlow<MindcardState> = _mindcardState.asStateFlow()

    fun loadMindcards() {
        viewModelScope.launch {
            _mindcardState.value = MindcardState.Loading
            try {
                // Por enquanto tá mockado, então não precisa de try/catch pra API
                val mindcardsList = mindcardRepository.getMindcards()
                _mindcards.value = mindcardsList
                _mindcardState.value = MindcardState.Success(mindcardsList)
            } catch (e: Exception) {
                _mindcardState.value = MindcardState.Error(e.message ?: "Erro ao carregar mindcards")
            }
        }
    }

    fun refreshMindcards() {
        loadMindcards()
    }
}

@Suppress("UNCHECKED_CAST")
class HomeViewModelFactory(private val repository: MindcardRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}