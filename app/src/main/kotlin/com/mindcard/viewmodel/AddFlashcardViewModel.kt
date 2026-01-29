package com.mindcard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindcard.data.repository.MindcardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import com.mindcard.data.model.MindcardItem

class AddFlashcardViewModel(private val repository: MindcardRepository) : ViewModel() {

    private val _deckTitle = MutableStateFlow("")
    val deckTitle = _deckTitle.asStateFlow()

    private val _cards = MutableStateFlow(listOf(createEmptyCard()))
    val cards = _cards.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private fun createEmptyCard() = MindcardItem(
        id = UUID.randomUUID().toString(),
        question = "",
        answer = ""
    )

    fun reset() {
        _deckTitle.value = ""
        _cards.value = listOf(createEmptyCard())
        _isLoading.value = false
    }

    fun onDeckTitleChange(newValue: String) { _deckTitle.value = newValue }
    fun addCardItem() { _cards.value = _cards.value + createEmptyCard() }
    fun removeCardItem(id: String) {
        if (_cards.value.size > 1) {
            _cards.value = _cards.value.filter { it.id != id }
        }
    }

    fun onCardChange(id: String, question: String, answer: String) {
        _cards.value = _cards.value.map {
            if (it.id == id) it.copy(question = question, answer = answer) else it
        }
    }

    fun saveDeck(onSuccess: () -> Unit) {
        val title = _deckTitle.value
        val currentCards = _cards.value
        if (title.isBlank() || currentCards.any { it.question.isBlank() || it.answer.isBlank() }) return

        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.saveDeckOnApi(title, currentCards)
            _isLoading.value = false
            result.onSuccess {
                reset() // Limpa os campos após salvar
                onSuccess()
            }
        }
    }
}
