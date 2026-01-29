package com.mindcard.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mindcard.ui.components.PrimaryButton
import com.mindcard.viewmodel.EditDeckViewModel
import com.mindcard.viewmodel.EditableCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDeckScreen(
    viewModel: EditDeckViewModel,
    onBackClick: () -> Unit,
    onPracticeClick: () -> Unit,
    onDeleteSuccess: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val deckTitle by viewModel.deckTitle.collectAsState()
    val cards by viewModel.cards.collectAsState()
    val hasChanges by viewModel.hasChanges.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Deletar Deck") },
            text = { Text("Tem certeza que deseja deletar este deck? Esta ação não pode ser desfeita.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteDeck(onDeleteSuccess)
                    }
                ) {
                    Text("Deletar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Deck") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    // Botão Salvar - habilitado apenas se houver alterações
                    TextButton(
                        onClick = { viewModel.saveChanges(onSaveSuccess) },
                        enabled = hasChanges && !isLoading
                    ) {
                        Text(
                            "Salvar", 
                            color = if (hasChanges && !isLoading) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.addNewCard() }) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Carta")
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
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
                        onValueChange = { viewModel.onTitleChange(it) },
                        label = { Text("Nome do Deck") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                    
                    Text("Cartas", style = MaterialTheme.typography.titleMedium)
                }

                itemsIndexed(cards) { index, card ->
                    EditableCardItem(
                        index = index,
                        card = card,
                        onQuestionChange = { q -> viewModel.onCardChange(card.id, q, card.answer) },
                        onAnswerChange = { a -> viewModel.onCardChange(card.id, card.question, a) },
                        onRemove = { viewModel.deleteCard(card.id) },
                        canRemove = cards.size > 1
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Botão de Praticar - grande igual ao login
                    PrimaryButton(
                        text = "Praticar",
                        onClick = onPracticeClick,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !hasChanges && cards.isNotEmpty()
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Botão de deletar deck
                    OutlinedButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.Red
                        )
                    ) {
                        Icon(
                            Icons.Default.Delete, 
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Deletar Deck")
                    }
                    
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun EditableCardItem(
    index: Int,
    card: EditableCard,
    onQuestionChange: (String) -> Unit,
    onAnswerChange: (String) -> Unit,
    onRemove: () -> Unit,
    canRemove: Boolean
) {
    val cardColor = when {
        card.isNew -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        card.isModified -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Carta #${index + 1}", style = MaterialTheme.typography.labelLarge)
                    if (card.isNew) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Nova",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else if (card.isModified) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Modificada",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
                if (canRemove) {
                    IconButton(onClick = onRemove) {
                        Icon(Icons.Default.Delete, contentDescription = "Remover", tint = Color.Red)
                    }
                }
            }

            OutlinedTextField(
                value = card.question,
                onValueChange = onQuestionChange,
                label = { Text("Pergunta") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = card.answer,
                onValueChange = onAnswerChange,
                label = { Text("Resposta") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
