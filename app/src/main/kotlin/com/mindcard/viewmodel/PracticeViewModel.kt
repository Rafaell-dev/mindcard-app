package com.mindcard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindcard.data.model.Mindcard
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado da UI (Dados que a tela precisa desenhar)
data class PracticeUiState(
    val mindcard: Mindcard? = null,
    val currentIndex: Int = 0,
    val isAnswerVisible: Boolean = false,
    val correctCount: Int = 0,
    val isFinished: Boolean = false,
    val elapsedTimeSeconds: Long = 0 // <--- Novo campo para o Timer
)

class PracticeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PracticeUiState())
    val uiState = _uiState.asStateFlow()

    private var timerJob: Job? = null

    // Inicia a sessão de estudos
    fun startPractice(mindcard: Mindcard) {
        _uiState.value = PracticeUiState(mindcard = mindcard)
        startTimer() // Começa a contar!
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L) // Espera 1 segundo
                _uiState.update {
                    it.copy(elapsedTimeSeconds = it.elapsedTimeSeconds + 1)
                }
            }
        }
    }

    fun revealAnswer() {
        _uiState.update { it.copy(isAnswerVisible = true) }
    }

    fun markCorrect() {
        nextCard(isCorrect = true)
    }

    fun markIncorrect() {
        nextCard(isCorrect = false)
    }

    fun skip() {
        nextCard(isCorrect = false)
    }

    private fun nextCard(isCorrect: Boolean) {
        val currentState = _uiState.value
        val totalItems = currentState.mindcard?.items?.size ?: 0
        val nextIndex = currentState.currentIndex + 1

        if (nextIndex >= totalItems) {
            // Acabou o deck
            timerJob?.cancel() // Para o relógio
            _uiState.update {
                it.copy(
                    correctCount = if (isCorrect) it.correctCount + 1 else it.correctCount,
                    isFinished = true
                )
            }
        } else {
            // Próximo card
            _uiState.update {
                it.copy(
                    currentIndex = nextIndex,
                    isAnswerVisible = false,
                    correctCount = if (isCorrect) it.correctCount + 1 else it.correctCount
                )
            }
        }
    }

    // Formata os segundos para "MM:SS" (Ex: 65s -> "01:05")
    fun getFormattedTime(): String {
        val totalSeconds = _uiState.value.elapsedTimeSeconds
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "%02d:%02d".format(minutes, seconds)
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}