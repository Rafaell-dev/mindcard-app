package com.mindcard.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindcard.data.model.Mindcard
import com.mindcard.ui.components.MindcardCard
import com.mindcard.ui.theme.MindCardColors
import com.mindcard.ui.theme.MindCardTypography

@Composable
fun HomeScreen(
    userName: String,
    mindcards: List<Mindcard>,
    onMindcardClick: (Mindcard) -> Unit,
    onAddClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val cardColors = listOf(
        MindCardColors.OldFlax,
        MindCardColors.RocketOrange,
        MindCardColors.TaskBlue
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MindCardColors.Primary,
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar",
                    tint = MindCardColors.Onprimary
                )
            }
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Olá, $userName!", style = MindCardTypography.Heading2)
                        Text(
                            "Vamos estudar hoje?",
                            style = MindCardTypography.Body,
                            color = MindCardColors.MutedForeground
                        )
                    }

                    // BOTÃO DE LOGOUT COM COR VERMELHA VIVA
                    IconButton(
                        onClick = onLogoutClick,
                        modifier = Modifier.padding(end = 125.dp) // Mantive sua posição
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout",
                            tint = Color.Red, // Vermelho vivo
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            itemsIndexed(mindcards) { index, mindcard ->
                MindcardCard(
                    title = mindcard.title,
                    backgroundColor = cardColors[index % cardColors.size],
                    icon = { Text("📚", fontSize = 24.sp) },
                    onClick = { onMindcardClick(mindcard) }
                )
            }
        }
    }
}
