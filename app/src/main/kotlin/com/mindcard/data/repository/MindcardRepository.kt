package com.mindcard.data.repository

import com.mindcard.data.model.Mindcard
import com.mindcard.data.model.MindcardItem

class MindcardRepository {

    fun getMindcards(): List<Mindcard> {
        return listOf(
            // Item 1
            Mindcard("1", "Kotlin Basics", "Programação", listOf(
                MindcardItem("1", "O que é uma data class?", "Uma classe para armazenar dados."),
                MindcardItem("2", "Val vs Var?", "Val é imutável.")
            )),
            // Item 2
            Mindcard("2", "Jetpack Compose", "UI", listOf(
                MindcardItem("3", "Recomposition?", "Redesenhar a UI quando o estado muda.")
            )),
            // Item 3 (QUE ESTAVA FALTANDO)
            Mindcard("3", "Android Architecture", "Desenvolvimento", listOf(
                MindcardItem("5", "O que faz o ViewModel?", "Gerencia dados relacionados à UI de forma consciente do ciclo de vida.")
            ))
        )
    }

    suspend fun insertMindcard(mindcard: Mindcard) {
        println("SALVANDO NO MOCK: ${mindcard.title}")
    }
}