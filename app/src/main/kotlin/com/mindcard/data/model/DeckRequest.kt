package com.mindcard.data.model

import com.google.gson.annotations.SerializedName

data class DeckRequest(
    @SerializedName("titulo") val titulo: String,
    @SerializedName("flashcards") val flashcards: List<FlashcardRequest>
)

data class FlashcardRequest(
    @SerializedName("pergunta") val pergunta: String,
    @SerializedName("resposta") val resposta: String
)
