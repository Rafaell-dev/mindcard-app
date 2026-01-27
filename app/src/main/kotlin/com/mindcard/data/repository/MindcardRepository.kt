package com.mindcard.data.repository

import com.mindcard.data.model.Mindcard
import com.mindcard.data.model.MindcardItem

class MindcardRepository {
    private val _mindcards = listOf(
        Mindcard("1", "Kotlin Basics", "Programação", listOf(
            MindcardItem("1", "O que é uma data class?", "Uma classe usada principalmente para armazenar dados."),
            MindcardItem("2", "Como declarar uma variável imutável?", "Usando a palavra-chave 'val'.")
        )),
        Mindcard("2", "Jetpack Compose", "UI", listOf(
            MindcardItem("3", "O que é Recomposition?", "O processo de chamar as funções composable novamente quando os dados mudam."),
            MindcardItem("4", "Para que serve o remember?", "Para armazenar um valor na composição.")
        )),
        Mindcard("3", "Android Architecture", "Desenvolvimento", listOf(
            MindcardItem("5", "O que faz o ViewModel?", "Gerencia dados relacionados à UI de forma consciente do ciclo de vida.")
        ))
    )

    fun getMindcards(): List<Mindcard> {
        return _mindcards
    }

    fun getMindcard(id: String): Mindcard? {
        return _mindcards.find { it.id == id }
    }
}
