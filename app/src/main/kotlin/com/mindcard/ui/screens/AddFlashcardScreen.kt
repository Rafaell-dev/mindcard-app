package com.mindcard.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mindcard.data.model.MindcardItem
import com.mindcard.viewmodel.AddFlashcardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFlashcardScreen(
    viewModel: AddFlashcardViewModel,
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val deckTitle by viewModel.deckTitle.collectAsState()
    val cards by viewModel.cards.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Novo Deck") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.saveDeck(onSaveSuccess) }) {
                        Text("Salvar", color = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.addCardItem() }) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Carta")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = deckTitle,
                    onValueChange = { viewModel.onDeckTitleChange(it) },
                    label = { Text("Nome do Deck (Ex: Javascript)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                Text("Cartas", style = MaterialTheme.typography.titleMedium)
            }

            itemsIndexed(cards) { index, card ->
                CardItemInput(
                    index = index,
                    question = card.question,
                    answer = card.answer,
                    onQuestionChange = { q -> viewModel.onCardChange(card.id, q, card.answer) },
                    onAnswerChange = { a -> viewModel.onCardChange(card.id, card.question, a) },
                    onRemove = { viewModel.removeCardItem(card.id) },
                    canRemove = cards.size > 1
                )
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun CardItemInput(
    index: Int,
    question: String,
    answer: String,
    onQuestionChange: (String) -> Unit,
    onAnswerChange: (String) -> Unit,
    onRemove: () -> Unit,
    canRemove: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Carta #${index + 1}", style = MaterialTheme.typography.labelLarge)
                if (canRemove) {
                    IconButton(onClick = onRemove) {
                        Icon(Icons.Default.Delete, contentDescription = "Remover", tint = Color.Red)
                    }
                }
            }

            OutlinedTextField(
                value = question,
                onValueChange = onQuestionChange,
                label = { Text("Pergunta") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = answer,
                onValueChange = onAnswerChange,
                label = { Text("Resposta") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
