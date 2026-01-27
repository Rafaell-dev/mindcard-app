package com.mindcard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindcard.data.model.Mindcard
import com.mindcard.data.model.MindcardItem
import com.mindcard.data.repository.MindcardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class AddFlashcardViewModel(private val repository: MindcardRepository) : ViewModel() {

    private val _title = MutableStateFlow("")
    val title = _title.asStateFlow()

    private val _question = MutableStateFlow("")
    val question = _question.asStateFlow()

    private val _answer = MutableStateFlow("")
    val answer = _answer.asStateFlow()

    fun onTitleChange(newValue: String) { _title.value = newValue }
    fun onQuestionChange(newValue: String) { _question.value = newValue }
    fun onAnswerChange(newValue: String) { _answer.value = newValue }

    fun saveFlashcard(onSuccess: () -> Unit) {
        val currentTitle = _title.value
        val currentQuestion = _question.value
        val currentAnswer = _answer.value

        if (currentTitle.isBlank() || currentQuestion.isBlank() || currentAnswer.isBlank()) return

        viewModelScope.launch {
            // CORREÇÃO AQUI: Criando a estrutura completa que seu Model exige
            val newCard = Mindcard(
                id = UUID.randomUUID().toString(), // Gera um ID único automático
                title = currentTitle,
                category = "Geral", // Categoria padrão (já que não temos campo pra isso ainda)
                items = listOf(
                    MindcardItem(
                        id = UUID.randomUUID().toString(),
                        question = currentQuestion,
                        answer = currentAnswer
                    )
                )
            )

            repository.insertMindcard(newCard)
            onSuccess()
        }
    }
}