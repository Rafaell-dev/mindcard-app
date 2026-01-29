package com.mindcard.data.repository

import com.mindcard.data.local.SessionManager
import com.mindcard.data.model.DeckRequest
import com.mindcard.data.model.FlashcardRequest
import com.mindcard.data.model.Mindcard
import com.mindcard.data.model.MindcardItem
import com.mindcard.data.service.DeckService
import com.mindcard.data.service.NewFlashcardRequest
import com.mindcard.data.service.UpdateDeckRequest
import com.mindcard.data.service.UpdateFlashcardRequest
import com.mindcard.data.service.toMindcard

class MindcardRepository(
    private val sessionManager: SessionManager? = null,
    private val deckService: DeckService = DeckService { sessionManager?.fetchAuthToken() }
) {
    private var _cachedMindcards = listOf<Mindcard>()

    suspend fun getMindcards(): List<Mindcard> {
        return try {
            val response = deckService.api.listarDecks()
            if (response.isSuccessful) {
                _cachedMindcards = response.body()?.map { it.toMindcard() } ?: emptyList()
                _cachedMindcards
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getMindcard(id: String): Mindcard? {
        return _cachedMindcards.find { it.id == id } ?: getMindcards().find { it.id == id }
    }

    suspend fun saveDeckOnApi(title: String, cards: List<MindcardItem>): Result<Unit> {
        return try {
            val request = DeckRequest(
                titulo = title,
                flashcards = cards.map { FlashcardRequest(it.question, it.answer) }
            )

            val response = deckService.api.createDeck(request)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Erro ao cadastrar deck: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteDeck(id: String): Result<Unit> {
        return try {
            val response = deckService.api.deleteDeck(id)
            if (response.isSuccessful) {
                _cachedMindcards = _cachedMindcards.filter { it.id != id }
                Result.success(Unit)
            } else {
                Result.failure(Exception("Erro ao deletar deck: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateDeck(id: String, titulo: String, novosFlashcards: List<MindcardItem> = emptyList()): Result<Unit> {
        return try {
            val request = UpdateDeckRequest(
                titulo = titulo,
                novosFlashcards = novosFlashcards.map { NewFlashcardRequest(it.question, it.answer) }
            )
            val response = deckService.api.updateDeck(id, request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Erro ao atualizar deck: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteFlashcard(id: String): Result<Unit> {
        return try {
            val response = deckService.api.deleteFlashcard(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Erro ao deletar flashcard: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateFlashcard(id: String, pergunta: String, resposta: String): Result<Unit> {
        return try {
            val request = UpdateFlashcardRequest(pergunta = pergunta, resposta = resposta)
            val response = deckService.api.updateFlashcard(id, request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Erro ao atualizar flashcard: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

