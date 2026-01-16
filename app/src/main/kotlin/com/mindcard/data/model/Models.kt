package com.mindcard.data.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val photoUrl: String? = null
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
