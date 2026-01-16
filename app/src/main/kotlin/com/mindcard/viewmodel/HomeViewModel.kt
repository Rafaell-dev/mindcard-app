package com.mindcard.viewmodel

import androidx.lifecycle.ViewModel
import com.mindcard.data.model.Mindcard
import com.mindcard.data.repository.MindcardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel(private val repository: MindcardRepository) : ViewModel() {
    private val _mindcards = MutableStateFlow<List<Mindcard>>(emptyList())
    val mindcards: StateFlow<List<Mindcard>> = _mindcards

    init {
        loadMindcards()
    }

    private fun loadMindcards() {
        _mindcards.value = repository.getMindcards()
    }
}
