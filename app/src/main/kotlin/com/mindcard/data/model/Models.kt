package com.mindcard.data.model

data class User(
    val id: String,
    val nome: String,
    val email: String,
    val onboardingCompleto: Boolean = false,
)

data class Mindcard(
    val id: String,
    val title: String,
    val category: String,
    val items: List<MindcardItem> = emptyList()
)

data class MindcardItem(
    val id: String,
    val question: String,
    val answer: String,
    val difficulty: String = "Médio"
)
