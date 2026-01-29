package com.mindcard.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
    onRefreshClick: () -> Unit = {},
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
                        .padding(vertical = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Olá, $userName!", style = MindCardTypography.Heading2)
                        Text(
                            "Vamos estudar hoje?",
                            style = MindCardTypography.Body,
                            color = MindCardColors.MutedForeground
                        )
                    }
                    Row {
                        IconButton(onClick = onRefreshClick) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Atualizar",
                                tint = MindCardColors.Primary
                            )
                        }
                        IconButton(onClick = onLogoutClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Logout",
                                tint = Color.Red,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            if (mindcards.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("📭", fontSize = 48.sp)
                        Text(
                            "Nenhum deck encontrado",
                            style = MindCardTypography.Heading3,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                        Text(
                            "Clique no botão + para criar seu primeiro deck!",
                            style = MindCardTypography.Body,
                            color = MindCardColors.MutedForeground,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            } else {
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
}

