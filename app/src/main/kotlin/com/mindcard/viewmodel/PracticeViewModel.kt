package com.mindcard.viewmodel

import androidx.lifecycle.ViewModel
import com.mindcard.data.model.Mindcard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.mindcard.data.repository.MindcardRepository
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

data class PracticeState(
    val mindcard: Mindcard? = null,
    val currentIndex: Int = 0,
    val isAnswerVisible: Boolean = false,
    val correctCount: Int = 0,
    val skippedCount: Int = 0,
    val isFinished: Boolean = false
)

class PracticeViewModel(private val repository: MindcardRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(PracticeState())
    val uiState: StateFlow<PracticeState> = _uiState

    fun loadMindcard(id: String) {
        viewModelScope.launch {
            val mindcard = repository.getMindcard(id)
            if (mindcard != null) {
                _uiState.value = PracticeState(mindcard = mindcard)
            }
        }
    }

    fun revealAnswer() {
        _uiState.value = _uiState.value.copy(isAnswerVisible = true)
    }

    fun markCorrect() {
        val state = _uiState.value
        val nextIndex = state.currentIndex + 1
        val isFinished = nextIndex >= (state.mindcard?.items?.size ?: 0)
        
        _uiState.value = state.copy(
            correctCount = state.correctCount + 1,
            currentIndex = nextIndex,
            isAnswerVisible = false,
            isFinished = isFinished
        )
    }

    fun markIncorrect() {
        val state = _uiState.value
        val nextIndex = state.currentIndex + 1
        val isFinished = nextIndex >= (state.mindcard?.items?.size ?: 0)
        
        _uiState.value = state.copy(
            currentIndex = nextIndex,
            isAnswerVisible = false,
            isFinished = isFinished
        )
    }

    fun skip() {
        val state = _uiState.value
        val nextIndex = state.currentIndex + 1
        val isFinished = nextIndex >= (state.mindcard?.items?.size ?: 0)
        
        _uiState.value = state.copy(
            skippedCount = state.skippedCount + 1,
            currentIndex = nextIndex,
            isAnswerVisible = false,
            isFinished = isFinished
        )
    }
}
