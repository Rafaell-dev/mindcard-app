package com.mindcard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mindcard.data.model.MindcardItem
import com.mindcard.ui.components.Badge
import com.mindcard.ui.components.PrimaryButton
import com.mindcard.ui.components.SecondaryButton
import com.mindcard.ui.theme.MindCardColors
import com.mindcard.ui.theme.MindCardTypography

@Composable
fun PracticeScreen(
    item: MindcardItem?,
    currentIndex: Int,
    totalItems: Int,
    isAnswerVisible: Boolean,
    timerText: String,
    onRevealAnswer: () -> Unit,
    onCorrect: () -> Unit,
    onIncorrect: () -> Unit,
    onSkip: () -> Unit,
    onClose: () -> Unit
) {
    if (item == null) return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Fechar")
            }

            // Progress Bar
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp)
                    .padding(horizontal = 16.dp)
                    .background(MindCardColors.SmokyWhite, RoundedCornerShape(4.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = (currentIndex + 1).toFloat() / totalItems)
                        .fillMaxHeight()
                        .background(MindCardColors.JetBlack, RoundedCornerShape(4.dp))
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // RF01: Centralizando os Badges e o Timer
        Row(
            modifier = Modifier.fillMaxWidth(),
            // Ajuste aqui: Mantém o espaçamento de 8dp, mas alinha o grupo todo ao Centro
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Badge(text = item.difficulty)
            // RF04: O Timer já está aqui visualmente, lógica virá depois
            Badge(text = timerText)
            Badge(text = "${currentIndex + 1}/$totalItems")
        }

        Spacer(Modifier.height(32.dp))

        // Question/Answer Card
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(24.dp))
                .border(2.dp, MindCardColors.Border, RoundedCornerShape(24.dp))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = item.question,
                    style = MindCardTypography.Heading2,
                    textAlign = TextAlign.Center
                )

                if (isAnswerVisible) {
                    Spacer(Modifier.height(24.dp))
                    HorizontalDivider(color = MindCardColors.Border)
                    Spacer(Modifier.height(24.dp))
                    Text(
                        text = item.answer,
                        style = MindCardTypography.Body,
                        color = MindCardColors.MutedForeground,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        // Buttons
        if (!isAnswerVisible) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SecondaryButton(
                    text = "Pular",
                    onClick = onSkip,
                    modifier = Modifier.weight(1f)
                )
                PrimaryButton(
                    text = "Verificar",
                    onClick = onRevealAnswer,
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            // RF02: Botões de Errei/Acertei estilizados
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onIncorrect,
                    modifier = Modifier.weight(1f).height(48.dp),
                    // Vermelho para erro
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("❌ Errei", fontWeight = FontWeight.Bold, color = Color.White)
                }
                Button(
                    onClick = onCorrect,
                    modifier = Modifier.weight(1f).height(48.dp),
                    // Verde suave para sucesso
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("✅ Acertei", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}