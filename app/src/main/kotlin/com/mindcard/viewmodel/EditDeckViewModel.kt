package com.mindcard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindcard.data.model.Mindcard
import com.mindcard.data.model.MindcardItem
import com.mindcard.data.repository.MindcardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.UUID

data class EditableCard(
    val id: String,
    val question: String,
    val answer: String,
    val isNew: Boolean = false,
    val isModified: Boolean = false
)

class EditDeckViewModel(private val repository: MindcardRepository) : ViewModel() {

    private val _deckId = MutableStateFlow("")
    val deckId = _deckId.asStateFlow()
    
    private val _deckTitle = MutableStateFlow("")
    val deckTitle = _deckTitle.asStateFlow()
    
    private val _originalTitle = MutableStateFlow("")
    
    private val _cards = MutableStateFlow<List<EditableCard>>(emptyList())
    val cards = _cards.asStateFlow()
    
    private val _originalCards = MutableStateFlow<List<EditableCard>>(emptyList())
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    
    private val _hasChanges = MutableStateFlow(false)
    val hasChanges = _hasChanges.asStateFlow()
    
    private val _deletedFlashcardIds = MutableStateFlow<List<String>>(emptyList())

    fun loadDeck(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val mindcard = repository.getMindcard(id)
            if (mindcard != null) {
                _deckId.value = mindcard.id
                _deckTitle.value = mindcard.title
                _originalTitle.value = mindcard.title
                
                val editableCards = mindcard.items.map { item ->
                    EditableCard(
                        id = item.id,
                        question = item.question,
                        answer = item.answer,
                        isNew = false,
                        isModified = false
                    )
                }
                _cards.value = editableCards
                _originalCards.value = editableCards
                _deletedFlashcardIds.value = emptyList()
                updateHasChanges()
            }
            _isLoading.value = false
        }
    }
    
    fun onTitleChange(newTitle: String) {
        _deckTitle.value = newTitle
        updateHasChanges()
    }
    
    fun onCardChange(cardId: String, question: String, answer: String) {
        _cards.value = _cards.value.map { card ->
            if (card.id == cardId) {
                val originalCard = _originalCards.value.find { it.id == cardId }
                val isModified = originalCard == null || 
                    originalCard.question != question || 
                    originalCard.answer != answer
                card.copy(question = question, answer = answer, isModified = isModified)
            } else {
                card
            }
        }
        updateHasChanges()
    }
    
    fun addNewCard() {
        val newCard = EditableCard(
            id = UUID.randomUUID().toString(),
            question = "",
            answer = "",
            isNew = true,
            isModified = false
        )
        _cards.value = _cards.value + newCard
        updateHasChanges()
    }
    
    fun deleteCard(cardId: String) {
        val cardToDelete = _cards.value.find { it.id == cardId } ?: return
        
        // Só adiciona na lista de deletados se não for um card novo (que nunca foi salvo)
        if (!cardToDelete.isNew) {
            _deletedFlashcardIds.value = _deletedFlashcardIds.value + cardId
        }
        
        _cards.value = _cards.value.filter { it.id != cardId }
        updateHasChanges()
    }
    
    private fun updateHasChanges() {
        val titleChanged = _deckTitle.value != _originalTitle.value
        val cardsChanged = _cards.value.any { it.isModified || it.isNew }
        val cardsDeleted = _deletedFlashcardIds.value.isNotEmpty()
        _hasChanges.value = titleChanged || cardsChanged || cardsDeleted
    }
    
    fun saveChanges(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            
            var success = true
            
            // 1. Deletar flashcards marcados para deleção
            for (flashcardId in _deletedFlashcardIds.value) {
                val result = repository.deleteFlashcard(flashcardId)
                if (result.isFailure) {
                    success = false
                    break
                }
            }
            
            // 2. Atualizar flashcards modificados (não novos)
            if (success) {
                for (card in _cards.value.filter { it.isModified && !it.isNew }) {
                    val result = repository.updateFlashcard(card.id, card.question, card.answer)
                    if (result.isFailure) {
                        success = false
                        break
                    }
                }
            }
            
            // 3. Atualizar deck (título e novos flashcards)
            if (success) {
                val newCards = _cards.value.filter { it.isNew }.map { card ->
                    MindcardItem(id = card.id, question = card.question, answer = card.answer)
                }
                val result = repository.updateDeck(_deckId.value, _deckTitle.value, newCards)
                if (result.isFailure) {
                    success = false
                }
            }
            
            _isLoading.value = false
            
            if (success) {
                onSuccess()
            }
        }
    }
    
    fun deleteDeck(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.deleteDeck(_deckId.value)
            _isLoading.value = false
            if (result.isSuccess) {
                onSuccess()
            }
        }
    }
}
